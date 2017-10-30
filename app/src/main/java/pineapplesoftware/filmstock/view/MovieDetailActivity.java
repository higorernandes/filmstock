package pineapplesoftware.filmstock.view;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import pineapplesoftware.filmstock.R;
import pineapplesoftware.filmstock.model.dto.Movie;

public class MovieDetailActivity extends AppCompatActivity implements View.OnClickListener
{
    //region Attributes

    private static final String TITLE = "TITLE";
    private static final String YEAR = "YEAR";
    private static final String RATED = "RATED";
    private static final String RELEASED = "RELEASED";
    private static final String RUNTIME = "RUNTIME";
    private static final String GENRE = "GENRE";
    private static final String DIRECTOR = "DIRECTOR";
    private static final String WRITER = "WRITER";
    private static final String ACTORS = "ACTORS";
    private static final String PLOT = "PLOT";
    private static final String LANGUAGE = "LANGUAGE";
    private static final String COUNTRY = "COUNTRY";
    private static final String AWARDS = "AWARDS";
    private static final String POSTER_URL = "POSTER_URL";
    private static final String METASCORE = "METASCORE";
    private static final String IMDB_RATING = "IMDB_RATING";
    private static final String IMDB_VOTES = "IMDB_VOTES";
    private static final String IMDB_ID = "IMDB_ID";
    private static final String TYPE = "TYPE";
    private static final String DVD = "DVD";
    private static final String BOX_OFFICE = "BOX_OFFICE";
    private static final String PRODUCTION = "PRODUCTION";
    private static final String WEBSITE = "WEBSITE";

    private Toolbar mToolbar;
    private TextView mToolbarTitle;
    private Button mToolbarLikeButton;

    //endregion

    //region Constructors

    public static Intent getActivityIntent(Context context, Movie movie) {
        Intent intent = new Intent(context, MovieDetailActivity.class);
        intent.putExtra(TITLE, movie.getTitle());
        intent.putExtra(YEAR, movie.getYear());
        intent.putExtra(RATED, movie.getRated());
        intent.putExtra(RELEASED, movie.getReleased());
        intent.putExtra(RUNTIME, movie.getRuntime());
        intent.putExtra(GENRE, movie.getGenre());
        intent.putExtra(DIRECTOR, movie.getDirector());
        intent.putExtra(WRITER, movie.getWriter());
        intent.putExtra(ACTORS, movie.getActors());
        intent.putExtra(PLOT, movie.getPlot());
        intent.putExtra(LANGUAGE, movie.getLanguage());
        intent.putExtra(COUNTRY, movie.getCountry());
        intent.putExtra(AWARDS, movie.getAwards());
        intent.putExtra(POSTER_URL, movie.getPosterUrl());
        intent.putExtra(METASCORE, movie.getMetascore());
        intent.putExtra(IMDB_RATING, movie.getImdbRating());
        intent.putExtra(IMDB_VOTES, movie.getImdbVotes());
        intent.putExtra(IMDB_ID, movie.getImdbId());
        intent.putExtra(TYPE, movie.getType());
        intent.putExtra(DVD, movie.getDvd());
        intent.putExtra(BOX_OFFICE, movie.getBoxOffice());
        intent.putExtra(PRODUCTION, movie.getProduction());
        intent.putExtra(WEBSITE, movie.getWebsite());

        return intent;
    }

    //endregion

    //region Overridden Methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        initViews();
        setUpToolbar();
        loadMovieInformation();
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_like_button:
                saveMovieToLocalDatabase();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out_right);
    }

    //endregion

    //region Private Methods

    private void initViews() {
        mToolbar = findViewById(R.id.detail_toolbar);
        mToolbarTitle = mToolbar.findViewById(R.id.toolbar_title);
        mToolbarLikeButton = mToolbar.findViewById(R.id.toolbar_like_button);

        //region Main information

        LinearLayout containerView;

        // Poster.
        String posterUrl = getIntent().getStringExtra(POSTER_URL);
        ImageView moviePosterImageView = findViewById(R.id.detail_movie_image);
        if (posterUrl != null && !posterUrl.isEmpty() && !posterUrl.toLowerCase().equals("n/a")) {
            Glide.with(this).load(posterUrl).into(moviePosterImageView);
        }

        // Title.
        String title = getIntent().getStringExtra(TITLE);
        TextView movieTitleTextView = findViewById(R.id.detail_movie_name);
        if (title != null && !title.isEmpty() && !title.toLowerCase().equals("n/a")) {
            movieTitleTextView.setText(title);
        }

        // Type.
        String type = getIntent().getStringExtra(TYPE);
        TextView movieTypeTextView = findViewById(R.id.detail_movie_type);
        if (type != null && !type.isEmpty() && !type.toLowerCase().equals("n/a")) {
            movieTypeTextView.setText(type);
        } else {
            movieTypeTextView.setVisibility(View.GONE);
        }

        // Genre.
        String genre = getIntent().getStringExtra(GENRE);
        if (genre != null && !genre.isEmpty() && !genre.toLowerCase().equals("n/a")) {
            TextView movieGenreTextView = findViewById(R.id.detail_movie_genre_text);
            movieGenreTextView.setText(genre);
        }

        // Year and Runtime/duration.
        String year = getIntent().getStringExtra(YEAR);
        String runtime = getIntent().getStringExtra(RUNTIME);
        TextView movieDurationTextView = findViewById(R.id.detail_movie_duration);
        if ((year != null && !year.isEmpty() && !year.toLowerCase().equals("n/a")) && (runtime != null && !runtime.isEmpty() && !runtime.toLowerCase().equals("n/a"))) {
            movieDurationTextView.setText(getResources().getString(R.string.movie_detail_movie_year_runtime)
                    .replace("{movieYear}", year)
                    .replace("{runtime}", runtime));
        } else if ((year == null || year.isEmpty() || year.toLowerCase().equals("n/a")) && (runtime != null && !runtime.isEmpty() && !runtime.toLowerCase().equals("n/a"))) {
            movieDurationTextView.setText(runtime);
        } else if ((year != null && !year.isEmpty() && !year.toLowerCase().equals("n/a")) && (runtime == null || runtime.isEmpty() || runtime.toLowerCase().equals("n/a"))) {
            movieDurationTextView.setText(year);
        }

        // Plot.
        String plot = getIntent().getStringExtra(PLOT);
        if (plot != null && !plot.isEmpty() && !plot.toLowerCase().equals("n/a")) {
            TextView moviePlotTextView = findViewById(R.id.detail_movie_plot);
            moviePlotTextView.setText(plot);
        }

        // Cast.
        String cast = getIntent().getStringExtra(ACTORS);
        if (cast != null && !cast.isEmpty() && !cast.toLowerCase().equals("n/a")) {
            TextView movieDirectorTextView = findViewById(R.id.detail_movie_actors_text);
            movieDirectorTextView.setText(cast);
        } else {
            containerView = findViewById(R.id.detail_actors_container);
            containerView.setVisibility(View.GONE);
        }

        // Director.
        String director = getIntent().getStringExtra(DIRECTOR);
        if (director != null && !director.isEmpty() && !director.toLowerCase().equals("n/a")) {
            TextView movieDirectorTextView = findViewById(R.id.detail_movie_director_text);
            movieDirectorTextView.setText(director);
        } else {
            containerView = findViewById(R.id.detail_director_container);
            containerView.setVisibility(View.GONE);
        }

        //endregion

        //region Secondary information

        // Writers.
        String writer = getIntent().getStringExtra(WRITER);
        if (writer != null && !writer.isEmpty() && !writer.toLowerCase().equals("n/a")) {
            TextView movieWriterTextView = findViewById(R.id.detail_movie_writer_text);
            movieWriterTextView.setText(writer);
        } else {
            containerView = findViewById(R.id.detail_writer_container);
            containerView.setVisibility(View.GONE);
        }

        // Rated.
        String rated = getIntent().getStringExtra(RATED);
        if (rated != null && !rated.isEmpty() && !rated.toLowerCase().equals("n/a")) {
            TextView movieRatedTextView = findViewById(R.id.detail_movie_rated_text);
            movieRatedTextView.setText(rated);
        } else {
            containerView = findViewById(R.id.detail_rated_container);
            containerView.setVisibility(View.GONE);
        }

        // Awards.
        String awards = getIntent().getStringExtra(AWARDS);
        if (awards != null && !awards.isEmpty() && !awards.toLowerCase().equals("n/a")) {
            TextView movieAwardsTextView = findViewById(R.id.detail_movie_awards_text);
            movieAwardsTextView.setText(awards);
        } else {
            containerView = findViewById(R.id.detail_awards_container);
            containerView.setVisibility(View.GONE);
        }

        // Language.
        String language = getIntent().getStringExtra(LANGUAGE);
        if (language != null && !language.isEmpty() && !language.toLowerCase().equals("n/a")) {
            TextView movieLanguageTextView = findViewById(R.id.detail_movie_language_text);
            movieLanguageTextView.setText(language);
        } else {
            containerView = findViewById(R.id.detail_language_container);
            containerView.setVisibility(View.GONE);
        }

        // Country.
        String country = getIntent().getStringExtra(COUNTRY);
        if (country != null && !country.isEmpty() && !country.toLowerCase().equals("n/a")) {
            TextView movieCountryTextView = findViewById(R.id.detail_movie_country_text);
            movieCountryTextView.setText(country);
        } else {
            containerView = findViewById(R.id.detail_country_container);
            containerView.setVisibility(View.GONE);
        }

        // Production.
        String production = getIntent().getStringExtra(PRODUCTION);
        if (production != null && !production.isEmpty() && !production.toLowerCase().equals("n/a")) {
            TextView movieProductionTextView = findViewById(R.id.detail_movie_production_text);
            movieProductionTextView.setText(production);
        } else {
            containerView = findViewById(R.id.detail_production_container);
            containerView.setVisibility(View.GONE);
        }

        // Box Office.
        String boxOffice = getIntent().getStringExtra(BOX_OFFICE);
        if (boxOffice != null && !boxOffice.isEmpty() && !boxOffice.toLowerCase().equals("n/a")) {
            TextView movieBoxOfficeTextView = findViewById(R.id.detail_movie_box_text);
            movieBoxOfficeTextView.setText(boxOffice);
        } else {
            containerView = findViewById(R.id.detail_boxoffice_container);
            containerView.setVisibility(View.GONE);
        }

        // IMDB Rating.
        String imdbRating = getIntent().getStringExtra(IMDB_RATING);
        if (imdbRating != null && !imdbRating.isEmpty() && !imdbRating.toLowerCase().equals("n/a")) {
            TextView movieRatingsTextView = findViewById(R.id.detail_movie_ratings_text);
            movieRatingsTextView.setText(imdbRating);
        } else {
            containerView = findViewById(R.id.detail_ratings_container);
            containerView.setVisibility(View.GONE);
        }

        //endregion

        mToolbarLikeButton.setOnClickListener(this);
    }

    private void setUpToolbar() {
        setSupportActionBar(mToolbar);
        mToolbarTitle.setText(getResources().getString(R.string.movie_detail_text));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mToolbarLikeButton.setVisibility(View.VISIBLE);
    }

    private void loadMovieInformation() {

    }

    private void saveMovieToLocalDatabase() {

    }

    //endregion
}
