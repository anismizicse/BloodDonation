package com.ourdreamit.blooddonationproject.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import com.ourdreamit.blooddonationproject.utils.MySingleton;
import com.ourdreamit.blooddonationproject.ui.fragments.NavigationDrawerFragment;
import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.utils.SetToolbar;
import com.ourdreamit.blooddonationproject.dialogs.AllDialog;
import com.ourdreamit.blooddonationproject.ui.adapters.DonorListAdapter;
import com.ourdreamit.blooddonationproject.utils.DataLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DonorsList extends AppCompatActivity {
    public RecyclerView donorsList;
    public DonorListAdapter donorListAdapter;
    ProgressDialog dialog;
    TextView notfound_err;
    private Toolbar toolbar;
    public static String donorBlood,blood_bags,hospital, uDivision, uDistrict, uThana;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.donors_list);

        notfound_err = (TextView) findViewById(R.id.notfound_err);

        dialog = new AllDialog(this).showProgressDialog("",
                "Searching Donors...", true, true);

        dialog.show();

        donorsList = (RecyclerView) findViewById(R.id.donorList);
        setDonorList();

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        SetToolbar.context = this;
        getSupportActionBar().setTitle(SetToolbar.setTitle());
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(SetToolbar.setBgColor()));

        NavigationDrawerFragment drawerFragment =
                (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_drawer);
        drawerFragment.setUp(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        DataLoader.checkLogin(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id){

            case R.id.action_logout:
                DataLoader.context = this;
                DataLoader.removeLocalVars();
                //new SharedPrefUtil(this).saveString(DataLoader.LOGOUT, "true");
                Intent intent = new Intent(this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;

        }
        return true;
    }

    private void setDonorList() {

        DataLoader.context = this;
        String url = getString(R.string.server_url) + "donors_by_address.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DataLoader.userDetails =  new ArrayList<>();

                try {

                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject mJsonObject;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        mJsonObject = jsonArray.getJSONObject(i);

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

                        DataLoader.userDetails.add(userInfo);

                    }


                    dialog.hide();
                    donorListAdapter = new DonorListAdapter(DonorsList.this,DataLoader.userDetails,getFragmentManager());
                    donorsList.setAdapter(donorListAdapter);
                    donorsList.setLayoutManager(new LinearLayoutManager(DonorsList.this));

                    if(DataLoader.userDetails.size() == 0){
                        notfound_err.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DonorsList.this, "Couldn't read url", Toast.LENGTH_SHORT).show();
                //Log.d("donorsByAddress", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("phone", DataLoader.getUserPhone());
                params.put("donorBlood", donorBlood);
                params.put("uDivision", uDivision);
                params.put("uDistrict", uDistrict);
                params.put("uThana", uThana);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(this).addToRequest(stringRequest);



    }

}

