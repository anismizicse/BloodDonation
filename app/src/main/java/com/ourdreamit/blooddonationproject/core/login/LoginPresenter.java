package com.ourdreamit.blooddonationproject.core.login;

import android.app.Activity;
import android.util.Log;


public class LoginPresenter implements LoginContract.Presenter, LoginContract.OnLoginListener {
    private LoginContract.View mLoginView;
    private LoginInteractor mLoginInteractor;

    public LoginPresenter(LoginContract.View loginView) {
        //Log.d("ChatTrace","LoginPresenter LoginPresenter");
        this.mLoginView = loginView;
        mLoginInteractor = new LoginInteractor(this);
    }

    @Override
    public void login(Activity activity, String email, String password) {
        //Log.d("ChatTrace","LoginPresenter login");
        mLoginInteractor.performFirebaseLogin(activity, email, password);
    }

    @Override
    public void onSuccess(String message) {
        //Log.d("ChatTrace","LoginPresenter onSuccess");
        mLoginView.onLoginSuccess(message);
    }

    @Override
    public void onFailure(String message) {
        //Log.d("ChatTrace","LoginPresenter onFailure");
        mLoginView.onLoginFailure(message);
    }
}
