package com.ourdreamit.blooddonationproject.ui.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ourdreamit.blooddonationproject.dialogs.AllDialog;
import com.ourdreamit.blooddonationproject.ui.activities.SplashActivity;
import com.ourdreamit.blooddonationproject.utils.DataLoader;
import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.utils.MySingleton;
import com.ourdreamit.blooddonationproject.utils.SharedPrefUtil;

import java.util.HashMap;
import java.util.Map;

public class DoctorCarePhones extends Fragment implements View.OnClickListener {
    AppCompatButton direct_call,ambulance;
    CardView chatAvailability;

    public  static Boolean doctor = false;
    SwitchCompat liveChat;
    int chatStatus;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.doctor_care_phones, container, false);

        direct_call = (AppCompatButton) view.findViewById(R.id.direct_call);
        ambulance = (AppCompatButton) view.findViewById(R.id.ambulance);
        chatAvailability = (CardView) view.findViewById(R.id.chatAvailability);
        liveChat = (SwitchCompat) view.findViewById(R.id.liveChat);

        if(doctor) {
            direct_call.setVisibility(View.GONE);
            ambulance.setVisibility(View.GONE);

            chatAvailability.setVisibility(View.VISIBLE);
            chatAvailability.setOnClickListener(this);

            liveChat.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //is chkIos checked?
                    if (liveChat.isChecked()) {
                        liveChat.setChecked(true);
                        chatStatus = 1;
                        new AllDialog(getActivity()).showDialog("LiveChat Online.", "Are you sure to go online ?", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                chatAvailability();
                            }
                        },"Yes","No");

                    }
                    else {
                        liveChat.setChecked(false);
                        chatStatus = 0;
                        new AllDialog(getActivity()).showDialog("LiveChat Offline.", "Are you sure to go Offline ?", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                chatAvailability();
                            }
                        },"Yes","No");

                    }

                }
            });


        }else{
            direct_call.setVisibility(View.VISIBLE);
            ambulance.setVisibility(View.VISIBLE);
            chatAvailability.setVisibility(View.GONE);
            direct_call.setOnClickListener(this);
            ambulance.setOnClickListener(this);
        }

        setLiveChatSetting();


        return view;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id){
            case R.id.direct_call:

                DataLoader.context = getActivity();
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + DataLoader.getHotline())));

                break;
            case R.id.ambulance:

                DataLoader.context = getActivity();
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + DataLoader.getAmbulane())));

                break;
        }
    }

    private void setLiveChatSetting() {
        String url = getString(R.string.server_url)+"get_doctor_livechat_settings.php";

        DataLoader.context = getActivity();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //Log.d("get_doctor_admins",response);
                    if(response.equals("1")){
                        chatStatus = 1;
                        liveChat.setChecked(true);
                    }else if(response.equals("0")){
                        chatStatus = 0;
                        liveChat.setChecked(false);

                        DataLoader.livechatlogin = "false";
                        new SharedPrefUtil(getActivity()).saveString(DataLoader.LIVECHATLOGIN, "false");
                    }


                } catch (Exception e) {
                    //Log.d("get_doctor_admins",e.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    Toast.makeText(getActivity(), "Couldn't read url", Toast.LENGTH_SHORT).show();
                }catch (Exception e){

                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("phone", DataLoader.getUserPhone());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(getActivity()).addToRequest(stringRequest);
    }

    private void chatAvailability() {
        String app_server_url = getString(R.string.server_url) + "update_doctor_livechat.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, app_server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response.equals("updated")) {
                            if(chatStatus == 1) {
                                Toast.makeText(getActivity(), "You are Online now.", Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(getActivity(), "You are Offline now.", Toast.LENGTH_LONG).show();
                            }

                        }else if(response.equals("chatregistration")){

//                            DataLoader.livechatlogin = "false";
//                            new SharedPrefUtil(getActivity()).saveString(DataLoader.LIVECHATLOGIN, "false");

                            new AllDialog(getActivity()).showDialog("LiveChat login required.", "You must login or register to livechat.", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    startActivity(new Intent(getActivity(), SplashActivity.class));

                                }
                            },"Login","No");

                        } else{
                            new AllDialog(getActivity()).showDialog("Error.","Sorry your livechat status was not changed.",null,null,"OK");

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
                params.put("phone", DataLoader.getUserPhone());
                params.put("status", ""+chatStatus);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(getActivity()).addToRequest(stringRequest);
    }
}
