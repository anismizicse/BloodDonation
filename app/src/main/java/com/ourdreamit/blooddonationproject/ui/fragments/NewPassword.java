package com.ourdreamit.blooddonationproject.ui.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import com.ourdreamit.blooddonationproject.ui.activities.Login;
import com.ourdreamit.blooddonationproject.utils.MySingleton;
import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.dialogs.AllDialog;
import com.ourdreamit.blooddonationproject.utils.SharedPrefUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by anismizi on 7/12/17.
 */

public class NewPassword extends Fragment implements View.OnClickListener,View.OnTouchListener {
    EditText newPass, confirmPass;
    TextView newpass_error, confirmpass_error;
    Button changePass;
    String newPassword, confirmPassword;
    ProgressDialog dialog;
    android.app.FragmentManager manager;
    RelativeLayout newPassRoot;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.newpassword_fragment, container, false);

        newPassRoot = (RelativeLayout) view.findViewById(R.id.newPassRoot);
        newPassRoot.setOnTouchListener(this);

        manager = getActivity().getFragmentManager();

        newPass = (EditText) view.findViewById(R.id.newPass);
        confirmPass = (EditText) view.findViewById(R.id.confirmPass);

        newpass_error = (TextView) view.findViewById(R.id.newpass_error);
        confirmpass_error = (TextView) view.findViewById(R.id.confirmpass_error);

        changePass = (Button) view.findViewById(R.id.changePass);
        changePass.setOnClickListener(this);

        dialog = new AllDialog(getActivity()).showProgressDialog("","Verfiying...",true,false);

        return view;
    }

    @Override
    public void onClick(View v) {
        v.startAnimation(DataLoader.buttonClick);

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(v.getContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);

        newPassword = newPass.getText().toString();
        confirmPassword = confirmPass.getText().toString();

        int id = v.getId();
        switch (id) {
            case R.id.changePass:
                if (newPass.getText().toString().isEmpty()) {
                    newpass_error.setText("Password can't be empty");
                    newpass_error.setVisibility(View.VISIBLE);
                } else if (newPassword.length() < 6) {
                    newpass_error.setText("Password should be at least 6 characters long");
                    newpass_error.setVisibility(View.VISIBLE);
                } else if (confirmPass.getText().toString().isEmpty()) {
                    newpass_error.setVisibility(View.GONE);
                    confirmpass_error.setText("Confirm password Can't be empty");
                    confirmpass_error.setVisibility(View.VISIBLE);
                } else if (!confirmPassword.equals(newPassword)) {
                    newpass_error.setVisibility(View.GONE);
                    confirmpass_error.setText("Confirm password didn't match");
                    confirmpass_error.setVisibility(View.VISIBLE);
                }else{
                    newpass_error.setVisibility(View.GONE);
                    confirmpass_error.setVisibility(View.GONE);
                    dialog.show();
                    //Log.d("changePass",DataLoader.getUserPhone()+" "+newPassword);
                    String app_server_url = getString(R.string.server_url)+"change_password.php";
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, app_server_url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    dialog.hide();
                                    //if (response.equals("updated")) {
                                        Toast.makeText(getActivity(),"Password was changed Successfully",Toast.LENGTH_LONG).show();
                                        new SharedPrefUtil(getActivity()).saveString(DataLoader.PASSWORD, newPassword);
                                        startActivity(new Intent(getActivity(), Login.class));


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
                            params.put("phone", DataLoader.getUserPhone());
                            params.put("password", "" + newPassword);
                            return params;
                        }
                    };
                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    MySingleton.getmInstance(getActivity()).addToRequest(stringRequest);
                }
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(v.getContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
        return true;
    }
}
