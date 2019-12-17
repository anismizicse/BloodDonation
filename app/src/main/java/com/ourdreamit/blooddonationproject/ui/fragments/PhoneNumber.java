package com.ourdreamit.blooddonationproject.ui.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.fragment.app.Fragment;
import android.app.ProgressDialog;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import androidx.core.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.ui.activities.VerifyPhone;
import com.ourdreamit.blooddonationproject.dialogs.AllDialog;
import com.ourdreamit.blooddonationproject.ui.activities.BasicInformation;
import com.ourdreamit.blooddonationproject.ui.activities.Login;
import com.ourdreamit.blooddonationproject.utils.DataLoader;
import com.ourdreamit.blooddonationproject.utils.MySingleton;
import com.ourdreamit.blooddonationproject.utils.SharedPrefUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

public class PhoneNumber extends Fragment implements View.OnClickListener,View.OnTouchListener {
    EditText phoneNumber;
    TextView phone_error;
    String mobileNumber;
    ProgressDialog dialog;

    Button sendCode;

    int min = 12345;
    int max = 99999;
    Random rand = new Random();
    int code;

    android.app.FragmentManager manager;


    RelativeLayout phoneroot;
    Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.phone_number, container, false);

        phoneroot = (RelativeLayout) view.findViewById(R.id.phoneroot);
        phoneroot.setOnTouchListener(this);

        phoneNumber = (EditText) view.findViewById(R.id.phoneNumber);
        phone_error = (TextView) view.findViewById(R.id.phone_error);

        manager = getActivity().getFragmentManager();

        sendCode = (Button) view.findViewById(R.id.sendCode);
        sendCode.setOnClickListener(this);

        dialog = new AllDialog(getActivity()).showProgressDialog("","Registering...",true,false);

        rand = new Random();
        code = rand.nextInt(max - min + 1) + min;


        return view;
    }

    @Override
    public void onClick(View v) {
        //v.startAnimation(DataLoader.buttonClick);
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(v.getContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
        mobileNumber = phoneNumber.getText().toString();
        if (phoneNumber.getText().toString().isEmpty()) {
            phone_error.setText("Phone number can't be empty");
            phone_error.setVisibility(View.VISIBLE);
        } else if (!Pattern.matches("[a-zA-Z]+", mobileNumber)) {

            if (mobileNumber.length() == 11 && mobileNumber != null && mobileNumber.charAt(0) == '0'
                    && mobileNumber.charAt(1) == '1' && getCarrier(2)) {
                phone_error.setVisibility(View.GONE);
                internetChecking();


            } else if (mobileNumber.length() == 14 && mobileNumber != null && mobileNumber.charAt(3) == '0'
                    && mobileNumber.charAt(4) == '1' && getCarrier(5)) {
                phone_error.setVisibility(View.GONE);
                mobileNumber = mobileNumber.substring(3);
                internetChecking();

            } else {
                phone_error.setText("Invalid phone number");
                phone_error.setVisibility(View.VISIBLE);
            }
        } else {
            phone_error.setText("Invalid Pphone number");
            phone_error.setVisibility(View.VISIBLE);
        }
    }

    private void internetChecking() {
        DataLoader.context = getActivity();
        if (DataLoader.checkInternet() && mobileNumber != null) {
            dialog.show();
            insertUserData();
        } else {
            phone_error.setVisibility(View.GONE);
            new AllDialog(getActivity()).showDialog("","Please connect to Internet to setup your account",null,null,"OK");

        }
    }



    private void insertUserData() {

        context = getActivity();
        String app_server_url = getString(R.string.server_url) + "register_new_user.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, app_server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response.equals("basicinfo")) {
                            dialog.hide();
                            new SharedPrefUtil(getActivity()).saveString(DataLoader.PHONE_NUMBER, mobileNumber);
                            DataLoader.userPhone = mobileNumber;
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.remove(fragmentManager.findFragmentByTag("phoneNumber"));
                            fragmentTransaction.commit();
                            startActivity(new Intent(getActivity(), BasicInformation.class));
                            getActivity().finish();


                        }else if (response.equals("login")) {
                            dialog.hide();
                            new AllDialog(getActivity()).showDialog("", mobileNumber + " has been used to register an account. Please login.", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(getActivity(), Login.class));
                                }
                            },"Login","Cancel");

                        } else if (response.equals("inserted")) {

                            new SharedPrefUtil(getActivity()).saveString(DataLoader.PHONE_NUMBER, mobileNumber);
                            dialog.hide();
                            DataLoader.userPhone = mobileNumber;
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            VerifyPhone fragment = new VerifyPhone();
                            fragmentTransaction.remove(fragmentManager.findFragmentByTag("phoneNumber"));
                            fragmentTransaction.add(R.id.signup_container, fragment, "verifyPhone");
                            fragmentTransaction.commit();
                            Toast.makeText(getActivity(), "SMS Sent", Toast.LENGTH_SHORT).show();
                            //sendNotification();
                            //sendSms();
                            DataLoader.context = context;
                            DataLoader.sendSms(mobileNumber,""+code,"signUp");
                        } else {
                            Toast.makeText(getActivity(), "Error Occured, Please check internet connection", Toast.LENGTH_LONG).show();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Network Error", Toast.LENGTH_LONG).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("code", "" + code);
                params.put("phone", mobileNumber);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(getActivity()).addToRequest(stringRequest);
    }



    public boolean getCarrier(int pos) {
        if (mobileNumber.charAt(pos) == '3' || mobileNumber.charAt(pos) == '4' ||
                mobileNumber.charAt(pos) == '5' || mobileNumber.charAt(pos) == '6' || mobileNumber.charAt(pos) == '7' ||
                mobileNumber.charAt(pos) == '8' || mobileNumber.charAt(pos) == '9')
            return true;


        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(v.getContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
        return true;
    }

}
