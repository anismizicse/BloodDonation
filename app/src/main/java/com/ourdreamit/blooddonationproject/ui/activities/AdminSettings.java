package com.ourdreamit.blooddonationproject.ui.activities;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ourdreamit.blooddonationproject.ui.adapters.AdminsListAdapter;
import com.ourdreamit.blooddonationproject.utils.DataLoader;
import com.ourdreamit.blooddonationproject.ui.adapters.DoctorsListAdapter;
import com.ourdreamit.blooddonationproject.utils.MySingleton;
import com.ourdreamit.blooddonationproject.ui.fragments.NavigationDrawerFragment;
import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.dialogs.AllDialog;
import com.ourdreamit.blooddonationproject.dialogs.ChangeHotline;
import com.ourdreamit.blooddonationproject.utils.SetToolbar;
import com.ourdreamit.blooddonationproject.utils.SharedPrefUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminSettings extends AppCompatActivity implements ChangeHotline.SettingsAction {
    private Toolbar toolbar;
    FragmentManager manager;
    public RecyclerView doctorsList,adminsList;
    public DoctorsListAdapter doctorsListAdapter;
    public AdminsListAdapter adminsListAdapter;
    ProgressDialog dialog;
    public static TextView hotlinePhone,ambulancePhone;
    String hotlineNum,ambulanceNum;
    SwitchCompat liveChat;
    int chatStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_settings);

        adminsList = (RecyclerView) findViewById(R.id.adminsList);
        doctorsList = (RecyclerView) findViewById(R.id.doctorsList);

        liveChat = (SwitchCompat) findViewById(R.id.liveChat);
        liveChat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //is chkIos checked?
                if (liveChat.isChecked()) {
                    liveChat.setChecked(true);
                    chatStatus = 1;
                    new AllDialog(AdminSettings.this).showDialog("LiveChat Online.", "Are you sure to go online ?", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            chatAvailability();
                        }
                    },"Yes","No");

                }
                else {
                    liveChat.setChecked(false);
                    chatStatus = 0;
                    new AllDialog(AdminSettings.this).showDialog("LiveChat Offline.", "Are you sure to go Offline ?", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            chatAvailability();
                        }
                    },"Yes","No");

                }

            }
        });


        hotlinePhone = (TextView) findViewById(R.id.hotlinePhone);
        ambulancePhone = (TextView) findViewById(R.id.ambulancePhone);

        manager = getFragmentManager();

        try {
            toolbar = (Toolbar) findViewById(R.id.app_bar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            SetToolbar.context = this;
            getSupportActionBar().setTitle(SetToolbar.setTitle());
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(SetToolbar.setBgColor()));
        }catch (Exception e){

        }

        NavigationDrawerFragment drawerFragment =
                (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_drawer);
        drawerFragment.setUp(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        dialog = new AllDialog(this).showProgressDialog("","Loading...",true,true);

        dialog.show();
        setLiveChatSetting();
        setHotlineNumbers();
        setDoctorsAdmins();

        DataLoader.checkLogin(this);
    }

    private void setLiveChatSetting() {
        String url = getString(R.string.server_url)+"get_livechat_settings.php";

        DataLoader.context = this;
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
                        new SharedPrefUtil(AdminSettings.this).saveString(DataLoader.LIVECHATLOGIN, "false");
                    }


                } catch (Exception e) {
                    //Log.d("get_doctor_admins",e.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AdminSettings.this,"Couldn't read url",Toast.LENGTH_SHORT).show();
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
        MySingleton.getmInstance(this).addToRequest(stringRequest);
    }

    private void chatAvailability() {
        String app_server_url = getString(R.string.server_url) + "update_livechat.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, app_server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.d("myStatus", DataLoader.getUserPhone() + " " + profile_visible + " " + autopro_visible + " " + response.toString());
                        dialog.hide();
                        if (response.equals("updated")) {
                            if(chatStatus == 1) {

                                Toast.makeText(AdminSettings.this, "You are Online now.", Toast.LENGTH_LONG).show();

                            }else{

                                Toast.makeText(AdminSettings.this, "You are Offline now.", Toast.LENGTH_LONG).show();
                            }

                        }else if(response.equals("chatregistration")){

//                            DataLoader.livechatlogin = "false";
//                            new SharedPrefUtil(AdminSettings.this).saveString(DataLoader.LIVECHATLOGIN, "false");

                            new AllDialog(AdminSettings.this).showDialog("LiveChat login required.", "You must login or register to livechat.", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    startActivity(new Intent(AdminSettings.this, SplashActivity.class));

                                }
                            },"Login","No");

                        } else{
                            new AllDialog(AdminSettings.this).showDialog("Error.","Sorry your livechat status was not changed.",null,null,"OK");

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
        MySingleton.getmInstance(this).addToRequest(stringRequest);
    }

    public void addDoctor(View view){
        view.startAnimation(DataLoader.buttonClick);
        ChangeHotline.context = this;
        ChangeHotline.add_doctor = true;
        ChangeHotline.hotline = ChangeHotline.add_admin =  ChangeHotline.ambulance = false;
        ChangeHotline hotline = new ChangeHotline();
        hotline.show(getFragmentManager(),"addDoctor");

        //Log.d("url",url);

    }




    public void addAdmin(View view){
        view.startAnimation(DataLoader.buttonClick);
        ChangeHotline.context = this;
        ChangeHotline.add_admin = true;
        ChangeHotline.hotline = ChangeHotline.add_doctor =  ChangeHotline.ambulance = false;
        ChangeHotline hotline = new ChangeHotline();
        hotline.show(getFragmentManager(),"addAdmin");
    }

    private void setHotlineNumbers() {
        String url = getString(R.string.server_url)+"get_admin_settings.php";
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

                        hotlineNum = mJsonObject.getString("hotline_phone");
                        ambulanceNum = mJsonObject.getString("ambulance_phone");

                    }
                    hotlinePhone.setText(hotlineNum);
                    ambulancePhone.setText(ambulanceNum);

                } catch (JSONException e) {
                    //Log.d("get_doctor_admins",e.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AdminSettings.this,"Couldn't read url",Toast.LENGTH_SHORT).show();
            }
        }){
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

    private void setDoctorsAdmins() {
        String url = getString(R.string.server_url)+"get_doctor_admins.php";

        DataLoader.context = this;
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.hide();
                try {
                    //Log.d("get_doctor_admins",response);
                    DataLoader.activeDoctorDetails =  new ArrayList<>();
                    DataLoader.adminDetails =  new ArrayList<>();

                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject mJsonObject;
                    //Log.d("SearchDonorFragment","user size "+ jsonArray.length());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        mJsonObject = jsonArray.getJSONObject(i);
                        //Log.d("SearchDonorFragment","iteration "+ i);
                        //insert userinfo into userDetails list arrays
                        DataLoader.UserInfo userInfo = new DataLoader.UserInfo();
                        //pic_path,fname,lname,email,phone,birthDate,bloodgroup,location,latitude,longitude
                        userInfo.phone = mJsonObject.getString("phone");
                        userInfo.pic_path = mJsonObject.getString("pic_path");
                        userInfo.fname = mJsonObject.getString("fname");
                        userInfo.lname = mJsonObject.getString("lname");
                        userInfo.blood_group = mJsonObject.getString("blood_group");
                        userInfo.birth_date = mJsonObject.getString("birth_date");
                        userInfo.age = mJsonObject.getString("age");
                        userInfo.last_donation = mJsonObject.getString("last_donation");
                        userInfo.new_donor = mJsonObject.getString("new_donor");
                        userInfo.email = mJsonObject.getString("email");
                        userInfo.division = mJsonObject.getString("division");
                        userInfo.district = mJsonObject.getString("district");
                        userInfo.upazila = mJsonObject.getString("upazila");

                        userInfo.address = mJsonObject.getString("address");
                        userInfo.fcm_email = mJsonObject.getString("fcm_email");
                        userInfo.fcm_uid = mJsonObject.getString("fcm_uid");
                        userInfo.fcm_token = mJsonObject.getString("fcm_token");


                        userInfo.pro_visible = mJsonObject.getString("pro_visible");
                        userInfo.called_date = mJsonObject.getString("called_date");
                        userInfo.called_today = mJsonObject.getString("called_today");
                        userInfo.donations_number = mJsonObject.getString("donations_number");
                        userInfo.user_type = mJsonObject.getString("user_type");
                        userInfo.gender = mJsonObject.getString("gender");
                        userInfo.already_donated = mJsonObject.getString("already_donated");
                        userInfo.autopro_visible = mJsonObject.getString("autopro_visible");

                        if(userInfo.user_type.equals("doctor"))
                            DataLoader.activeDoctorDetails.add(userInfo);
                        else
                            DataLoader.adminDetails.add(userInfo);
                        //Log.d("AllDoctorsAdmins",mJsonObject.toString());
                    }
                    dialog.hide();
                    //Log.d("get_doctor_admins",DataLoader.adminDetails.size()+" "+DataLoader.activeDoctorDetails.size());
                    doctorsListAdapter = new DoctorsListAdapter(AdminSettings.this,DataLoader.activeDoctorDetails,getFragmentManager());
                    doctorsList.setAdapter(doctorsListAdapter);
                    doctorsList.setLayoutManager(new LinearLayoutManager(AdminSettings.this));

                    adminsListAdapter = new AdminsListAdapter(AdminSettings.this,DataLoader.adminDetails,getFragmentManager());
                    adminsList.setAdapter(adminsListAdapter);
                    adminsList.setLayoutManager(new LinearLayoutManager(AdminSettings.this));

                } catch (JSONException e) {
                    //Log.d("get_doctor_admins",e.toString());
                }
                //Log.d("AllDonors",response);
                //prepareNearbyPlaces(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AdminSettings.this,"Network Error",Toast.LENGTH_SHORT).show();
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(this).addToRequest(stringRequest);
    }


    public void changeHotline(View view){
        ChangeHotline.context = this;
        view.startAnimation(DataLoader.buttonClick);
        ChangeHotline.hotline = true;
        ChangeHotline.ambulance = ChangeHotline.add_admin =  ChangeHotline.add_doctor = false;
        ChangeHotline hotline = new ChangeHotline();
        hotline.show(getFragmentManager(),"hotline");
    }

    public void changeAmbulance(View view){
        ChangeHotline.context = this;
        view.startAnimation(DataLoader.buttonClick);
        ChangeHotline.ambulance = true;
        ChangeHotline.hotline = ChangeHotline.add_admin =  ChangeHotline.add_doctor = false;
        ChangeHotline hotline = new ChangeHotline();
        hotline.show(getFragmentManager(),"hotline");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_logout:
                //new SharedPrefUtil(this).saveString(DataLoader.LOGOUT, "true");
                DataLoader.context = this;
                DataLoader.removeLocalVars();

                Intent intent = new Intent(this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;

        }
        return true;
    }

    @Override
    public void performAction() {

        if(ChangeHotline.add_doctor){
            doctorsListAdapter.addActiveDoctor(ChangeHotline.profileInfo);
            //Log.d("adminDoctor",ChangeHotline.profileInfo.pic_path+" "+ChangeHotline.add_admin+" "+ChangeHotline.add_doctor);
        }else if (ChangeHotline.add_admin){
            adminsListAdapter.addAdmin(ChangeHotline.profileInfo);
        }
    }


}
