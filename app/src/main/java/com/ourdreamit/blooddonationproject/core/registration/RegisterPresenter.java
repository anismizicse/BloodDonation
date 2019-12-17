package com.ourdreamit.blooddonationproject.core.registration;

import android.app.Activity;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;


public class RegisterPresenter implements RegisterContract.Presenter, RegisterContract.OnRegistrationListener {
    private RegisterContract.View mRegisterView;
    private RegisterInteractor mRegisterInteractor;

    public RegisterPresenter(RegisterContract.View registerView) {
        //Log.d("ChatTrace","RegisterPresenter RegisterPresenter");
        this.mRegisterView = registerView;
        mRegisterInteractor = new RegisterInteractor(this);
    }

    @Override
    public void register(Activity activity, String email, String password) {
        //Log.d("ChatTrace","RegisterPresenter register");
        mRegisterInteractor.performFirebaseRegistration(activity, email, password);
    }

    @Override
    public void onSuccess(FirebaseUser firebaseUser) {
        //Log.d("ChatTrace","RegisterPresenter onSuccess");
        mRegisterView.onRegistrationSuccess(firebaseUser);
    }

    @Override
    public void onFailure(String message) {
        //Log.d("ChatTrace","RegisterPresenter onFailure");
        mRegisterView.onRegistrationFailure(message);
    }
}
