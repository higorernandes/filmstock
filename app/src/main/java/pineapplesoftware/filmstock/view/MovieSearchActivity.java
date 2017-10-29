package pineapplesoftware.filmstock.view;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import pineapplesoftware.filmstock.R;
import pineapplesoftware.filmstock.adapter.SearchResultsArrayAdapter;
import pineapplesoftware.filmstock.adapter.SearchResultsArrayAdapter.IMovieSelectionListener;
import pineapplesoftware.filmstock.model.dto.Movie;
import pineapplesoftware.filmstock.presenter.MovieSearchPresenter;

public class MovieSearchActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher,
        IMovieSelectionListener, IMovieSearchView
{
    //region Attributes

    private Toolbar mToolbar;
    private EditText mToolbarSearchEditText;
    private ProgressBar mLoadingProgressBar;
    private RelativeLayout mToolbarSearchButton;

    private RelativeLayout mNoInternetView;
    private RelativeLayout mNoItemsView;

    private RecyclerView mSearchResultsRecyclerView;

    private SearchResultsArrayAdapter mSearchResultsArrayAdapter;
    private ArrayList<Movie> mSearchResults = new ArrayList<>();

    private MovieSearchPresenter mPresenter;

    private boolean mIsPerformingSearch = false;

    //endregion

    //region Constructors

    public static Intent getActivityIntent(Context context) {
        return new Intent(context, MovieSearchActivity.class);
    }

    //endregion

    //region Overridden Methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_search);

        mPresenter = new MovieSearchPresenter();
        mPresenter.setView(this);

        initViews();
        setUpToolbar();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.no_internet_reconnect_button:
                performSearch(mToolbarSearchEditText.getText().toString());
                break;
            case R.id.toolbar_search_relativelayout:
                performSearch(mToolbarSearchEditText.getText().toString());
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out_right);
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out_right);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (charSequence.length() >= 2) { // Minimum of 2 characters to start searching.
            performSearch(charSequence.toString());
        } else {
            mSearchResults.clear();
            mSearchResultsArrayAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void afterTextChanged(Editable editable) { }

    @Override
    public void onMovieItemSelected(int position) {
        startActivity(MovieDetailActivity.getActivityIntent(this, mSearchResults.get(position)));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out);
    }

    //region MovieSearchPresenter Methods

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void requestCallbackSuccess() { }

    @Override
    public void requestCallbackError() {
        hideLoading();
        mSearchResultsRecyclerView.setVisibility(View.GONE);
        mNoInternetView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLoading() {
        mIsPerformingSearch = true;
        mLoadingProgressBar.animate();
        mLoadingProgressBar.setVisibility(View.VISIBLE);
        mToolbarSearchButton.setVisibility(View.GONE);
    }

    @Override
    public void hideLoading() {
        mIsPerformingSearch = false;
        mLoadingProgressBar.clearAnimation();
        mLoadingProgressBar.setVisibility(View.GONE);
        mToolbarSearchButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void callbackSuccessSearchMovie(final ArrayList<Movie> movieList) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (movieList != null && movieList.size() > 0) {
                    mSearchResultsRecyclerView.setVisibility(View.VISIBLE);
                    mSearchResults = movieList;
                    mSearchResultsArrayAdapter.notifyDataSetChanged();
                } else {
                    mSearchResultsRecyclerView.setVisibility(View.GONE);
                    mNoItemsView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void callbackErrorSearchMovie() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mToolbarSearchButton.setVisibility(View.VISIBLE);
                mLoadingProgressBar.setVisibility(View.GONE);
                mLoadingProgressBar.clearAnimation();

                mSearchResultsRecyclerView.setVisibility(View.GONE);
                mNoItemsView.setVisibility(View.VISIBLE);
            }
        });
    }

    //endregion

    //endregion

    //region Private Methods

    private void initViews() {
        mToolbar = findViewById(R.id.search_toolbar);
        mToolbarSearchEditText = mToolbar.findViewById(R.id.toolbar_search_edittext);
        mLoadingProgressBar = findViewById(R.id.toolbar_progressbar);
        mToolbarSearchButton = findViewById(R.id.toolbar_search_relativelayout);

        mNoInternetView = findViewById(R.id.search_no_internet);
        mNoItemsView = findViewById(R.id.search_no_items);
        Button reconnectButton = mNoInternetView.findViewById(R.id.no_internet_reconnect_button);

        mSearchResultsRecyclerView = findViewById(R.id.search_movies_recyclerview);
        mSearchResultsArrayAdapter = new SearchResultsArrayAdapter(this, mSearchResults);
        mSearchResultsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mSearchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSearchResultsRecyclerView.setAdapter(mSearchResultsArrayAdapter);
        mSearchResultsArrayAdapter.setListener(this);

        reconnectButton.setOnClickListener(this);
        mToolbarSearchEditText.addTextChangedListener(this);
    }

    private void setUpToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void performSearch(String textToSearch) {
        if (!mIsPerformingSearch) {
            mPresenter.searchMovie(textToSearch);
        }
    }

    //endregion
}