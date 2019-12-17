package com.ourdreamit.blooddonationproject.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.dialogs.AllDialog;
import com.ourdreamit.blooddonationproject.ui.fragments.PhoneNumber;

public class SignUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        PhoneNumber fragment = new PhoneNumber();
        fragmentTransaction.add(R.id.signup_container, fragment,"phoneNumber");
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        try {
            new AllDialog(this).showDialog("", "Are you sure you want to cancel LifeCycle registration?", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(SignUp.this, Login.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }, "Yes", "No");
        }catch (Exception e){

        }

    }
}
