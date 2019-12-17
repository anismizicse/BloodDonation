package com.ourdreamit.blooddonationproject.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.utils.DataLoader;

public class LoginSignupOptions extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_signup_options);
    }

    public void login(View view) {
        view.startAnimation(DataLoader.buttonClick);
        startActivity(new Intent(this, Login.class));
    }

    public void signUp(View view) {
        view.startAnimation(DataLoader.buttonClick);
        startActivity(new Intent(this, SignUp.class));
    }
}
