package com.ourdreamit.blooddonationproject.ui.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.ourdreamit.blooddonationproject.utils.DataLoader;
import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.core.registration.RegisterContract;
import com.ourdreamit.blooddonationproject.core.registration.RegisterPresenter;
import com.ourdreamit.blooddonationproject.core.users.add.AddUserContract;
import com.ourdreamit.blooddonationproject.core.users.add.AddUserPresenter;
import com.ourdreamit.blooddonationproject.dialogs.AllDialog;
import com.ourdreamit.blooddonationproject.ui.activities.LoginActivity;


public class RegisterFragment extends Fragment implements View.OnClickListener, RegisterContract.View, AddUserContract.View {
    private static final String TAG = RegisterFragment.class.getSimpleName();

    private RegisterPresenter mRegisterPresenter;
    private AddUserPresenter mAddUserPresenter;

    private EditText mETxtEmail, mETxtPassword;
    private Button mBtnRegister;

    private ProgressDialog mProgressDialog;

    public static String fcm_email,fcm_uid;

    public static RegisterFragment newInstance() {
        //Log.d("ChatTrace","RegisterFragment newInstance");
        Bundle args = new Bundle();
        RegisterFragment fragment = new RegisterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Log.d("ChatTrace","RegisterFragment onCreateView");
        View fragmentView = inflater.inflate(R.layout.fragment_register, container, false);
        bindViews(fragmentView);
        return fragmentView;
    }

    private void bindViews(View view) {
        //Log.d("ChatTrace","RegisterFragment bindViews");
        mETxtEmail = (EditText) view.findViewById(R.id.edit_text_email_id);
        mETxtPassword = (EditText) view.findViewById(R.id.edit_text_password);
        mBtnRegister = (Button) view.findViewById(R.id.button_register);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Log.d("ChatTrace","RegisterFragment onActivityCreated");
        init();
    }

    private void init() {
        //Log.d("ChatTrace","RegisterFragment init");
        mRegisterPresenter = new RegisterPresenter(this);
        mAddUserPresenter = new AddUserPresenter(this);

        mProgressDialog = new AllDialog(getActivity()).showProgressDialog(getString(R.string.loading),getString(R.string.please_wait),true,true);


        mBtnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        DataLoader.hideKeyboard(view, getActivity());
        //Log.d("ChatTrace","RegisterFragment onClick");
        int viewId = view.getId();

        switch (viewId) {
            case R.id.button_register:
                onRegister(view);
                break;
        }
    }

    private void onRegister(View view) {
        //Log.d("ChatTrace","RegisterFragment onRegister");
        String emailId = mETxtEmail.getText().toString();
        String password = mETxtPassword.getText().toString();
        if (!emailId.equals("") && !password.equals("")) {
            mRegisterPresenter.register(getActivity(), emailId, password);
            mProgressDialog.show();
        }else{
            new AllDialog(getActivity()).showDialog("Error !","Email or password field is empty.",null,null,"OK");
        }
    }

    @Override
    public void onRegistrationSuccess(FirebaseUser firebaseUser) {
        //Log.d("ChatTrace","RegisterFragment onRegistrationSuccess");
        mProgressDialog.setMessage(getString(R.string.adding_user_to_db));
        Toast.makeText(getActivity(), "LiveChat Registration Successful!", Toast.LENGTH_SHORT).show();
        mAddUserPresenter.addUser(getActivity().getApplicationContext(), firebaseUser);
    }

    @Override
    public void onRegistrationFailure(String message) {
        //Log.d("ChatTrace","RegisterFragment onRegistrationFailure");
        mProgressDialog.dismiss();
        mProgressDialog.setMessage(getString(R.string.please_wait));
        //Log.e(TAG, "onRegistrationFailure: " + message);
        Toast.makeText(getActivity(), "LiveChat Registration failed!+\n" + message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAddUserSuccess(String message) {
        //Log.d("ChatTrace","RegisterFragment onAddUserSuccess");
        mProgressDialog.dismiss();
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        DataLoader.context = getActivity();
        DataLoader.updateFcmInfo("RegisterFragment");
        LoginActivity.startIntent(getActivity());

    }



    @Override
    public void onAddUserFailure(String message) {
        //Log.d("ChatTrace","RegisterFragment onAddUserFailure");
        mProgressDialog.dismiss();
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
