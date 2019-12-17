package com.ourdreamit.blooddonationproject.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.dialogs.AllDialog;
import com.ourdreamit.blooddonationproject.fcm.DeleteTokenService;
import com.ourdreamit.blooddonationproject.utils.DataLoader;
import com.ourdreamit.blooddonationproject.utils.MySingleton;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class VerifyPhone extends Fragment implements View.OnClickListener,View.OnTouchListener{
    EditText codeNumber;
    static ProgressDialog dialog;
    static String code;
    TextView code_error,codeSentTo,resendCode;
    Button codeVerify;
    static android.app.FragmentManager manager;

    int min = 12345;
    int max = 99999;
    Random rand = new Random();
    static int newCode;
    static Context context;
    RelativeLayout verifyroot;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.verify_phone, container, false);

        verifyroot = (RelativeLayout) view.findViewById(R.id.verifyroot);
        verifyroot.setOnTouchListener(this);

        context = getActivity();

        codeNumber = (EditText) view.findViewById(R.id.codeNumber);
        code_error = (TextView) view.findViewById(R.id.code_error);
        codeSentTo = (TextView) view.findViewById(R.id.codeSentTo);
        resendCode = (TextView) view.findViewById(R.id.resendCode);
        DataLoader.context = context;
        codeSentTo.setText("We just sent you a verfication code to "+DataLoader.getUserPhone());

        manager = getActivity().getFragmentManager();

        codeVerify = (Button) view.findViewById(R.id.codeVerify);
        codeVerify.setOnClickListener(this);
        resendCode.setOnClickListener(this);

        dialog = new AllDialog(getActivity()).showProgressDialog("","Verfiying...",true,false);

        return view;
    }


    @Override
    public void onClick(View v) {
        //v.startAnimation(DataLoader.buttonClick);
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(v.getContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
        int id = v.getId();
        DataLoader.context = getActivity();
        switch (id){
            case R.id.codeVerify:
                verifyCode();
                break;
            case R.id.resendCode:


                rand = new Random();
                newCode = rand.nextInt(max - min + 1) + min;



                new AllDialog(getActivity()).showDialog("", "Do you want to get a new verification code?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resendNewCode();
                    }
                },"Yes","No");
                break;
        }


    }

    public static void resendNewCode() {
        String app_server_url = context.getString(R.string.server_url)+"resend_code.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, app_server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.d("phoneNumber",DataLoader.getUserPhone()+" "+code+" "+response.toString());
                        dialog.hide();
                        if (response.equals("updated")) {
                            //DataLoader.sendNotification(newCode);
                            DataLoader.context = context;
                            DataLoader.sendSms(DataLoader.getUserPhone(),""+newCode,"signUp");

                            new AllDialog(context).showDialog("","We have sent you a new verification code to verify your account",null,null,"OK");

                        } else if(response.equals("error")){
                            new AllDialog(context).showDialog("","Sorry ! We are having problem to send new code.Please try again later",null,null,"OK");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getActivity(),"Network Error",Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("phoneverify","true");
                params.put("code", "" + newCode);
                params.put("phone", DataLoader.getUserPhone());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(context).addToRequest(stringRequest);
    }

    private void verifyCode() {
        String app_server_url = getString(R.string.server_url)+"verify_phone.php";
        code = codeNumber.getText().toString();
        if(codeNumber.getText().toString().isEmpty()){
            code_error.setText("Please Enter Code");
            code_error.setVisibility(View.VISIBLE);
        } else if(code.length() != 5){
            code_error.setVisibility(View.VISIBLE);
        }else if(!DataLoader.checkInternet()){
            new AllDialog(getActivity()).showDialog("","Please connect to internet to verify phone number",null,null,"OK");
        } else {
            code_error.setVisibility(View.GONE);
            dialog.show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, app_server_url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //Log.d("phoneNumber",DataLoader.getUserPhone()+" "+code+" "+response.toString());
                            dialog.hide();
                            if (response.equals("updated")) {
                                Intent tokenRef = new Intent(getActivity(),DeleteTokenService.class);
                                getActivity().startService(tokenRef);
                                //startActivity(new Intent(getActivity(), MainActivity.class));
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                BasicInformation fragment = new BasicInformation();
                                fragmentTransaction.remove(fragmentManager.findFragmentByTag("verifyPhone"));
                                //fragmentTransaction.add(R.id.signup_container, fragment,"basicInformation");
                                fragmentTransaction.commit();
                                startActivity(new Intent(getActivity(), BasicInformation.class));
                                getActivity().finish();

                            } else if(response.equals("error")){
                                new AllDialog(getActivity()).showDialog("","Sorry ! Code Didn't match. Please try again.",null,null,"OK");
                            } else {
                                new AllDialog(getActivity()).showDialog("","Sorry ! We are having problem to verify code. Please try again later.",null,null,"OK");
                                //Toast.makeText(getActivity(),"Error Occured, Please check internet connection",Toast.LENGTH_LONG).show();

                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //Toast.makeText(getActivity(),"Network Error",Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    //params.put("phoneverify","true");
                    params.put("code", "" + code);
                    params.put("phone", DataLoader.getUserPhone());
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MySingleton.getmInstance(getActivity()).addToRequest(stringRequest);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(v.getContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
        return true;
    }

}
