package pineapplesoftware.filmstock.generic;

import android.content.Context;

/**
 * Created by root on 2017-10-29.
 */

public interface IView
{
    Context getContext();
    void requestCallbackSuccess();
    void requestCallbackError();
    void showLoading();
    void hideLoading();
}