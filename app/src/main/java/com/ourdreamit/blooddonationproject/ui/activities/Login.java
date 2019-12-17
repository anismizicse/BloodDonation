package com.ourdreamit.blooddonationproject.ui.activities;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.auth.FirebaseUser;
import com.ourdreamit.blooddonationproject.utils.MySingleton;
import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.core.login.LoginContract;
import com.ourdreamit.blooddonationproject.core.login.LoginPresenter;
import com.ourdreamit.blooddonationproject.core.registration.RegisterContract;
import com.ourdreamit.blooddonationproject.core.registration.RegisterPresenter;
import com.ourdreamit.blooddonationproject.core.users.add.AddUserContract;
import com.ourdreamit.blooddonationproject.core.users.add.AddUserPresenter;
import com.ourdreamit.blooddonationproject.dialogs.AllDialog;
import com.ourdreamit.blooddonationproject.fcm.DeleteTokenService;
import com.ourdreamit.blooddonationproject.utils.DataLoader;
import com.ourdreamit.blooddonationproject.utils.SharedPrefUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity implements LoginContract.View, RegisterContract.View,
        AddUserContract.View, DataLoader.TalkToProfileDetails, View.OnTouchListener {
    EditText phone, password;
    TextView phoneErr, passwordErr;
    String mobileNumber, userPass;
    FragmentManager manager;
    String fcmEmail, fcmUid, userEmail, userType, blood_group;

    private RegisterPresenter mRegisterPresenter;
    private AddUserPresenter mAddUserPresenter;
    private LoginPresenter mLoginPresenter;
    static ProgressDialog mProgressDialog;
    static Context context;
    ScrollView root;

    private static final String TAG = "Login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        root = (ScrollView) findViewById(R.id.login_root);
        root.setOnTouchListener(this);

        mRegisterPresenter = new RegisterPresenter(this);
        mAddUserPresenter = new AddUserPresenter(this);
        mLoginPresenter = new LoginPresenter(this);
        context = this;

        mProgressDialog = new AllDialog(this).showProgressDialog("Loggin In.Please Wait...",
                getString(R.string.please_wait), true, false);


        manager = getFragmentManager();

        phone = (EditText) findViewById(R.id.phone);
        password = (EditText) findViewById(R.id.password);

        phoneErr = (TextView) findViewById(R.id.phoneErr);
        passwordErr = (TextView) findViewById(R.id.passwordErr);
    }

    public void singUp(View view) {
        view.startAnimation(DataLoader.buttonClick);
        hideKeyboard(view);
        startActivity(new Intent(this, SignUp.class));
    }

    public void login(View view) {
        view.startAnimation(DataLoader.buttonClick);
        hideKeyboard(view);
        mobileNumber = phone.getText().toString();
        userPass = password.getText().toString();
        if (phone.getText().toString().isEmpty()) {
            phoneErr.setText("Phone number can't be empty");
            phoneErr.setVisibility(View.VISIBLE);
            passwordErr.setVisibility(View.GONE);
        } else if (password.getText().toString().isEmpty()) {
            passwordErr.setText("Password can't be empty");
            passwordErr.setVisibility(View.VISIBLE);
            phoneErr.setVisibility(View.GONE);
        } else if (!Pattern.matches("[a-zA-Z]+", mobileNumber)) {

            if (mobileNumber.length() == 11 && mobileNumber != null && mobileNumber.charAt(0) == '0'
                    && mobileNumber.charAt(1) == '1' && getCarrier(2)) {
                phoneErr.setVisibility(View.GONE);
                passwordErr.setVisibility(View.GONE);
                checkLogin();


            } else if (mobileNumber.length() == 14 && mobileNumber != null && mobileNumber.charAt(3) == '0'
                    && mobileNumber.charAt(4) == '1' && getCarrier(5)) {
                phoneErr.setVisibility(View.GONE);
                passwordErr.setVisibility(View.GONE);
                mobileNumber = mobileNumber.substring(3);
                checkLogin();


            } else {
                phoneErr.setText("Invalid phone number");
                phoneErr.setVisibility(View.VISIBLE);
                passwordErr.setVisibility(View.GONE);
            }
        } else {
            phoneErr.setText("Invalid phone number");
            phoneErr.setVisibility(View.VISIBLE);
        }
    }

    private void checkLogin() {
        mProgressDialog.show();
        DataLoader.context = this;

        if (!DataLoader.checkInternet()) {
            mProgressDialog.dismiss();
            new AllDialog(this).showDialog("", "Please connect to Internet to Login", null, null, "OK");

        } else {

            String app_server_url = getString(R.string.server_url) + "getcurrentuser.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, app_server_url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            //Log.d(TAG, "onResponse: "+response);

                            if (response.equals("na")) {
                                mProgressDialog.dismiss();
                                new AllDialog(Login.this).showDialog("Login Error !", "Phone or Password is incorrect !Please try again", null, null, "OK");

                            } else {
                                mProgressDialog.show();


                                try {

                                    JSONArray jsonArray = new JSONArray(response);
                                    JSONObject mJsonObject;
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        mJsonObject = jsonArray.getJSONObject(i);
                                        userType = mJsonObject.getString("user_type");
                                        fcmEmail = mJsonObject.getString("fcm_email");
                                        fcmUid = mJsonObject.getString("fcm_uid");
                                        userEmail = mJsonObject.getString("email");
                                        blood_group = mJsonObject.getString("blood_group");
                                        new SharedPrefUtil(Login.this).saveString(DataLoader.PHONE_NUMBER, mobileNumber);

                                        if ((fcmEmail.equals("na") || fcmEmail.equals("null")) ||
                                                (fcmUid.equals("na") || fcmUid.equals("null")) && !userEmail.equals("na")) {
                                            mRegisterPresenter.register(Login.this, userEmail, userPass);
                                        } else {
                                            setLocalVars();
                                        }


                                    }

                                } catch (Exception e) {
                                    mProgressDialog.dismiss();
                                    //e.printStackTrace();
                                }


                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            mProgressDialog.dismiss();
                            Toast.makeText(Login.this, "Network Error", Toast.LENGTH_LONG).show();
                            //Log.d(TAG, "onErrorResponse: "+error.toString());

                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("phone", "" + mobileNumber);
                    params.put("password", userPass);
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MySingleton.getmInstance(this).addToRequest(stringRequest);
        }


    }


    public void forgotPass(View view) {
        view.startAnimation(DataLoader.buttonClick);
        startActivity(new Intent(this, ResetPassword.class));
    }


    public boolean getCarrier(int pos) {

        if (mobileNumber.charAt(pos) == '5' || mobileNumber.charAt(pos) == '6' || mobileNumber.charAt(pos) == '7' ||
                mobileNumber.charAt(pos) == '8' || mobileNumber.charAt(pos) == '9')
            return true;


        return false;

    }

    @Override
    public void onRegistrationSuccess(FirebaseUser firebaseUser) {

        mProgressDialog.setMessage(getString(R.string.adding_user_to_db));
        Toast.makeText(this, "LiveChat Registration Successful!", Toast.LENGTH_SHORT).show();
        mAddUserPresenter.addUser(this.getApplicationContext(), firebaseUser);

    }

    @Override
    public void onRegistrationFailure(String message) {

        //mProgressDialog.dismiss();
        mProgressDialog.setMessage(getString(R.string.please_wait));

        new SharedPrefUtil(Login.this).saveString(DataLoader.LIVECHATLOGIN, "false");
        Toast.makeText(this, "LiveChat Registration failed!", Toast.LENGTH_SHORT).show();
        setUserDetails();
        //new AllDialog(this).showDialog("LiveChat Registration failed!",message,null,null,"OK");
    }

    @Override
    public void onAddUserSuccess(String message) {

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        DataLoader.context = this;
        DataLoader.updateFcmInfo("Login");
    }


    @Override
    public void onAddUserFailure(String message) {
        //Log.d("ChatTrace","RegisterFragment onAddUserFailure");
        //mProgressDialog.dismiss();

        mProgressDialog.setMessage(getString(R.string.please_wait));
        DataLoader.context = this;

        //DataLoader.removeLocalVars();
        setUserDetails();

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void setUserDetails() {
        //mProgressDialog.dismiss();
        setLocalVars();
    }

    private void setLocalVars() {

        setHotlineNumbers();

        Calendar cal = Calendar.getInstance();
        int yy = cal.get(Calendar.YEAR);
        int mm = 1 + cal.get(Calendar.MONTH);
        int dd = cal.get(Calendar.DAY_OF_MONTH);
        final String dateInString = dd + "-" + mm + "-" + yy;

        DataLoader.userPhone = mobileNumber;
        DataLoader.userPass = userPass;
        DataLoader.userType = userType;
        DataLoader.blood_group = blood_group;
        new SharedPrefUtil(Login.this).saveString(DataLoader.PHONE_NUMBER, mobileNumber);
        new SharedPrefUtil(Login.this).saveString(DataLoader.PASSWORD, userPass);
        new SharedPrefUtil(Login.this).saveString(DataLoader.USERTYPE, userType);
        new SharedPrefUtil(Login.this).saveString(DataLoader.BLOODGROUP, blood_group);
        new SharedPrefUtil(Login.this).saveString(DataLoader.LOGIN, "true");
        new SharedPrefUtil(Login.this).saveInt(DataLoader.NOTIFICATIONID, 0);
        new SharedPrefUtil(Login.this).saveInt(DataLoader.RECENTCHAT, 0);
        //Log.d("profileRequestAction",DataLoader.getUserPhone()+" "+DataLoader.getUserPass()+" "+DataLoader.getUserType()+" "+DataLoader.getUserBlood());

        new SharedPrefUtil(Login.this).saveString(DataLoader.PROCHECKDATE, dateInString);
        //new SharedPrefUtil(Login.this).saveInt(DataLoader.PROCHECKED, 0);

        Intent tokenRef = new Intent(Login.this, DeleteTokenService.class);
        startService(tokenRef);
        new SharedPrefUtil(Login.this).saveString(DataLoader.LOGOUT, "false");


    }

    private void setHotlineNumbers() {
        String url = getString(R.string.server_url) + "get_admin_settings.php";
        //Log.d("url",url);
        DataLoader.context = this;
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //Log.d("get_doctor_admins",response);

                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject mJsonObject;
                    //Log.d("SearchDonorFragment","user size "+ jsonArray.length());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        mJsonObject = jsonArray.getJSONObject(i);

                        DataLoader.hotline = mJsonObject.getString("hotline_phone");
                        DataLoader.ambulane = mJsonObject.getString("ambulance_phone");

                        new SharedPrefUtil(Login.this).saveString(DataLoader.HOTLINE, DataLoader.hotline);
                        new SharedPrefUtil(Login.this).saveString(DataLoader.AMBULANCE, DataLoader.ambulane);

                    }

                    //mLoginPresenter.login(Login.this, userEmail, userPass);

                    //Toast.makeText(Login.this, "Inside Else", Toast.LENGTH_SHORT).show();
                    if (fcmEmail.equals("na") || fcmEmail.equals("null") || fcmEmail.equals(""))
                        mLoginPresenter.login(Login.this, userEmail, userPass);
                    else if (userEmail.equals("na") || userEmail.equals("null") || userEmail.equals(""))
                        mLoginPresenter.login(Login.this, fcmEmail, userPass);
                    else
                        mLoginPresenter.login(Login.this, userEmail, userPass);


                } catch (JSONException e) {
                    //Log.d("get_doctor_admins",e.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Login.this, "Couldn't read url", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("request_type", "read");
                params.put("update_hotline", "na");
                params.put("update_ambulance", "na");
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(this).addToRequest(stringRequest);
    }

    @Override
    public void onLoginSuccess(String message) {

        DataLoader.context = this;
        DataLoader.updateFcmInfo("LoginActivityLoginSuccess");

        DataLoader.livechatlogin = "true";
        new SharedPrefUtil(Login.this).saveString(DataLoader.LIVECHATLOGIN, "true");
        //Log.d("ChatTrace","LoginFragment onLoginSuccess");
        if (userType.equals("admin")) {
            new SharedPrefUtil(this).saveString(DataLoader.ADMINCHATLOGIN, "true");
        }
        mProgressDialog.dismiss();
        Toast.makeText(this, "Livechat logged in successfully", Toast.LENGTH_SHORT).show();
        enterHome();
    }

    private void enterHome() {
        Intent intent = new Intent(Login.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onLoginFailure(String message) {
        //Log.d("ChatTrace","LoginFragment onLoginFailure");
        mProgressDialog.dismiss();
        //Toast.makeText(this, "Error: " + message, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Error: Live Chat login failed. You must login or register from Live Chat to get live support.", Toast.LENGTH_LONG).show();
        enterHome();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(v.getContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
        return false;
    }

    public void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(v.getContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
    }
}
