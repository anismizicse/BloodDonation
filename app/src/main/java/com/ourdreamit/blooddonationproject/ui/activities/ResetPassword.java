package com.ourdreamit.blooddonationproject.ui.activities;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.ui.fragments.ResetPassPhone;

public class ResetPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ResetPassPhone fragment = new ResetPassPhone();
        fragmentTransaction.add(R.id.password_reset_container, fragment,"resetPassPhone");
        fragmentTransaction.commit();
    }
}
