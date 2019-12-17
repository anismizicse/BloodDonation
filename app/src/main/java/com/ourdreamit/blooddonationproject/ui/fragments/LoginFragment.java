package com.ourdreamit.blooddonationproject.ui.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ourdreamit.blooddonationproject.ui.activities.AdminChatList;
import com.ourdreamit.blooddonationproject.ui.activities.AdminList;
import com.ourdreamit.blooddonationproject.utils.DataLoader;
import com.ourdreamit.blooddonationproject.ui.activities.DoctorCare;
import com.ourdreamit.blooddonationproject.ui.activities.FirebasePassReset;
import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.core.login.LoginContract;
import com.ourdreamit.blooddonationproject.core.login.LoginPresenter;
import com.ourdreamit.blooddonationproject.dialogs.AllDialog;
import com.ourdreamit.blooddonationproject.ui.activities.RegisterActivity;
import com.ourdreamit.blooddonationproject.utils.SharedPrefUtil;

public class LoginFragment extends Fragment implements View.OnClickListener, LoginContract.View{
    private LoginPresenter mLoginPresenter;

    private EditText mETxtEmail, mETxtPassword;
    private Button mBtnRegister;
    private AppCompatButton mBtnLogin;

    private ProgressDialog mProgressDialog;
    public static Context context;
    TextView forgot_chatpass;
    String userType;

    String emailId, password;

    public static String loginEmail = "";

    public static LoginFragment newInstance() {

        Bundle args = new Bundle();
        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Log.d("ChatTrace","LoginFragment onCreateView");
        View fragmentView = inflater.inflate(R.layout.fragment_login, container, false);
        bindViews(fragmentView);
        context = getActivity();
        return fragmentView;
    }

    private void bindViews(View view) {
        //Log.d("ChatTrace","LoginFragment bindViews");
        mETxtEmail = (EditText) view.findViewById(R.id.edit_text_email_id);
        mETxtPassword = (EditText) view.findViewById(R.id.edit_text_password);
        mBtnLogin = (AppCompatButton) view.findViewById(R.id.button_login);
        mBtnRegister = (Button) view.findViewById(R.id.button_register);
        forgot_chatpass = (TextView) view.findViewById(R.id.forgot_chatpass);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Log.d("ChatTrace","LoginFragment onActivityCreated");
        init();
    }

    private void init() {
        //Log.d("ChatTrace","LoginFragment init");
        mLoginPresenter = new LoginPresenter(this);

        mProgressDialog = new AllDialog(getActivity()).showProgressDialog(getString(R.string.loading),
                getString(R.string.please_wait),true,true);


        mBtnLogin.setOnClickListener(this);
        mBtnRegister.setOnClickListener(this);
        forgot_chatpass.setOnClickListener(this);

        setDummyCredentials();
    }

    private void setDummyCredentials() {
        //Log.d("ChatTrace","LoginFragment setDummyCredentials");
        //mETxtEmail.setText("test@test.com");
        //mETxtPassword.setText("123456");
    }

    @Override
    public void onClick(View view) {
        DataLoader.hideKeyboard(view, getActivity());
        //Log.d("ChatTrace","LoginFragment onClick");
        int viewId = view.getId();

        switch (viewId) {
            case R.id.button_login:
                onLogin(view);
                break;
            case R.id.button_register:
                onRegister(view);
                break;
            case R.id.forgot_chatpass:
                startActivity(new Intent(context, FirebasePassReset.class));
                break;
        }
    }

    private void onLogin(View view) {
        //Log.d("ChatTrace","LoginFragment onLogin");
        emailId = mETxtEmail.getText().toString();
        password = mETxtPassword.getText().toString();
        Log.d("Logfields",emailId+" "+password);
        if (!emailId.equals("") && !password.equals("")) {
            mLoginPresenter.login(getActivity(), emailId, password);
            mProgressDialog.show();
        }else{
            new AllDialog(getActivity()).showDialog("Error !","Email or password field is empty.",null,null,"OK");
        }
    }

    private void onRegister(View view) {
        //Log.d("ChatTrace","LoginFragment onRegister");
        RegisterActivity.startActivity(getActivity());
    }

    @Override
    public void onLoginSuccess(String message) {
        DataLoader.livechatlogin = "true";
        new SharedPrefUtil(getActivity()).saveString(DataLoader.LIVECHATLOGIN, "true");
        //Log.d("ChatTrace","LoginFragment onLoginSuccess");
        mProgressDialog.dismiss();
        Toast.makeText(getActivity(), "Logged in successfully", Toast.LENGTH_SHORT).show();

        DataLoader.context = getActivity();
        DataLoader.updateFcmInfo("LoginFragment");

        userType = new SharedPrefUtil(context).getString(DataLoader.USERTYPE, "na");
        if(userType.equals("admin") && !userType.equals("na") && !DataLoader.doctorCarePanel) {
            new SharedPrefUtil(context).saveString(DataLoader.ADMINCHATLOGIN, "true");
            Toast.makeText(getActivity(), "Logged in successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(),AdminChatList.class));
        }else if(userType.equals("donor") && !userType.equals("na") && !DataLoader.doctorCarePanel) {
            context = getActivity();
            DataLoader.context = getActivity();

            DataLoader.profileInfo = null;
            DataLoader.getUserFromServer("insertAdminChatList");
        }else if ((userType.equals("donor") || userType.equals("doctor")) && !userType.equals("na") &&
                DataLoader.doctorCarePanel) {
            Toast.makeText(getActivity(), "Logged in successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(),DoctorCare.class));
        }

        getActivity().finish();

    }

    public static void logintoChat(){

        context.startActivity(new Intent(context, AdminList.class));
    }


    @Override
    public void onLoginFailure(String message) {
        //Log.d("ChatTrace","LoginFragment onLoginFailure");
        mProgressDialog.dismiss();
        Toast.makeText(getActivity(), "Error: " + message, Toast.LENGTH_SHORT).show();
    }
}
