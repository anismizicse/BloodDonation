package com.ourdreamit.blooddonationproject.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ourdreamit.blooddonationproject.ui.adapters.AdminListAdapter;
import com.ourdreamit.blooddonationproject.utils.DataLoader;
import com.ourdreamit.blooddonationproject.utils.MySingleton;
import com.ourdreamit.blooddonationproject.ui.fragments.NavigationDrawerFragment;
import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.utils.SetToolbar;
import com.ourdreamit.blooddonationproject.dialogs.AllDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AdminList extends AppCompatActivity {
    public RecyclerView adminList;
    public AdminListAdapter adminListAdapter;
    private Toolbar toolbar;
    ProgressDialog dialog;

    TextView notfound_err;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_list);

        adminList = (RecyclerView) findViewById(R.id.adminList);
        notfound_err = (TextView) findViewById(R.id.notfound_err);

        dialog = new AllDialog(this).showProgressDialog("",
                "Loading Admin list...",true,true);
        dialog.show();


        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        SetToolbar.context = this;
        getSupportActionBar().setTitle(SetToolbar.setTitle());
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(SetToolbar.setBgColor()));

        NavigationDrawerFragment drawerFragment =
                (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_drawer);
        drawerFragment.setUp(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        setAdminList();

        DataLoader.checkLogin(this);
    }

    @Override
    public void onBackPressed() {
        try {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }catch (Exception e){

        }
    }



    private void setAdminList() {

        String url = getString(R.string.server_url) + "get_all_admins.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                DataLoader.adminDetails =  new ArrayList<>();

                try {

                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject mJsonObject;

                    if (jsonArray.length() == 0) {
                        notfound_err.setVisibility(View.VISIBLE);
                        adminList.setVisibility(View.GONE);
                    }

                    for (int i = 0; i < jsonArray.length(); i++) {
                        mJsonObject = jsonArray.getJSONObject(i);

                        DataLoader.UserInfo userInfo = new DataLoader.UserInfo();
                        //pic_path,fname,lname,email,phone,birthDate,bloodgroup,location,latitude,longitude
                        userInfo.phone = mJsonObject.getString("phone");
                        userInfo.password = mJsonObject.getString("password");
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


                        DataLoader.adminDetails.add(userInfo);

                    }


                    dialog.hide();
                    adminListAdapter = new AdminListAdapter(AdminList.this, DataLoader.adminDetails, getFragmentManager());
                    adminList.setAdapter(adminListAdapter);
                    adminList.setLayoutManager(new LinearLayoutManager(AdminList.this));


                } catch (JSONException e) {

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AdminList.this, "Couldn't load the list", Toast.LENGTH_SHORT).show();
                //Log.d("get_all_admins", error.toString());
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(this).addToRequest(stringRequest);


    }
}
