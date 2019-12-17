package com.ourdreamit.blooddonationproject.core.users.add;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;


public class AddUserPresenter implements AddUserContract.Presenter, AddUserContract.OnUserDatabaseListener {
    private AddUserContract.View mView;
    private AddUserInteractor mAddUserInteractor;

    public AddUserPresenter(AddUserContract.View view) {
        //Log.d("ChatTrace","AddUserPresenter AddUserPresenter");
        this.mView = view;
        mAddUserInteractor = new AddUserInteractor(this);
    }

    @Override
    public void addUser(Context context, FirebaseUser firebaseUser) {
        //Log.d("ChatTrace","AddUserPresenter addUser");
        mAddUserInteractor.addUserToDatabase(context, firebaseUser);
    }

    @Override
    public void onSuccess(String message) {
        //Log.d("ChatTrace","AddUserPresenter onSuccess");
        mView.onAddUserSuccess(message);
    }

    @Override
    public void onFailure(String message) {
        //Log.d("ChatTrace","AddUserPresenter onFailure");
        mView.onAddUserFailure(message);
    }
}
