package com.ourdreamit.blooddonationproject.ui.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.app.NotificationCompat;
import android.util.Log;
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
import com.ourdreamit.blooddonationproject.utils.DataLoader;
import com.ourdreamit.blooddonationproject.utils.MySingleton;
import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.dialogs.AllDialog;
import com.ourdreamit.blooddonationproject.utils.SharedPrefUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * Created by anismizi on 7/12/17.
 */

public class ResetPassPhone extends Fragment implements View.OnClickListener,View.OnTouchListener  {

    private static final String TAG = "ResetPassPhone";
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

    RelativeLayout resetPhoneroot;
    Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.resetpassphone_fragment, container, false);

        resetPhoneroot = (RelativeLayout) view.findViewById(R.id.resetPhoneroot);
        resetPhoneroot.setOnTouchListener(this);

        phoneNumber = (EditText) view.findViewById(R.id.phoneNumber);
        phone_error = (TextView) view.findViewById(R.id.phone_error);

        manager = getActivity().getFragmentManager();

        sendCode = (Button) view.findViewById(R.id.sendCode);
        sendCode.setOnClickListener(this);

        dialog = new AllDialog(getActivity()).showProgressDialog("","Sending Verification Code...",false,false);

        rand = new Random();
        code = rand.nextInt(max - min + 1) + min;


        return view;
    }

    @Override
    public void onClick(View v) {
        v.startAnimation(DataLoader.buttonClick);
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(v.getContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
        mobileNumber = phoneNumber.getText().toString();
        if (phoneNumber.getText().toString().isEmpty()) {
            phone_error.setText("Phone number can't be empty!");
            phone_error.setVisibility(View.VISIBLE);
        } else if (!Pattern.matches("[a-zA-Z]+", mobileNumber)) {

            if (mobileNumber.length() == 11 && mobileNumber != null && mobileNumber.charAt(0) == '0'
                    && mobileNumber.charAt(1) == '1' && getCarrier(2)) {
                phone_error.setVisibility(View.GONE);
                internetChecking();
                //L.t(this,"Valid");

            } else if (mobileNumber.length() == 14 && mobileNumber != null && mobileNumber.charAt(3) == '0'
                    && mobileNumber.charAt(4) == '1' && getCarrier(5)) {
                phone_error.setVisibility(View.GONE);
                mobileNumber = mobileNumber.substring(3);
                internetChecking();

            } else {
                phone_error.setText("Invalid phone number!");
                phone_error.setVisibility(View.VISIBLE);
            }
        } else {
            phone_error.setText("Invalid phone number!");
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
            new AllDialog(getActivity()).showDialog("","Please connect to Internet to reset account password",null,null,"OK");

        }
    }


    private void insertUserData() {
        context = getActivity();
        String app_server_url = getString(R.string.server_url) + "resetpass_code.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, app_server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response.equals("updated")) {
                            new SharedPrefUtil(getActivity()).saveString(DataLoader.PHONE_NUMBER, mobileNumber);
                            dialog.hide();
                            DataLoader.userPhone = mobileNumber;
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            VerifyResetCode fragment = new VerifyResetCode();
                            fragmentTransaction.remove(fragmentManager.findFragmentByTag("resetPassPhone"));
                            fragmentTransaction.add(R.id.password_reset_container, fragment, "verifyResetCode");
                            fragmentTransaction.commit();
                            Toast.makeText(getActivity(), "SMS Sent", Toast.LENGTH_SHORT).show();
                            //sendNotification();
                            DataLoader.context = context;
                            DataLoader.sendSms(mobileNumber,""+code,"resetPass");
                            //sendSms();
                        } else {
                            new AllDialog(getActivity()).showDialog("","Sorry! We couldn't find any account with this phone number.",null,null,"OK");

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Network Error", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "onErrorResponse: "+ error.getMessage());
                        
                        if(dialog.isShowing()){
                            dialog.dismiss();
                        }
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
