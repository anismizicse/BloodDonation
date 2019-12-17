package com.ourdreamit.blooddonationproject.ui.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ourdreamit.blooddonationproject.ui.fragments.AllSingleRequests;
import com.ourdreamit.blooddonationproject.ui.adapters.BloodRequestsListAdapter;
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
import java.util.HashMap;
import java.util.Map;

public class AllBloodRequests extends AppCompatActivity {
    public RecyclerView donorsList;
    public BloodRequestsListAdapter donorListAdapter;
    ProgressDialog dialog;
    private Toolbar toolbar;
    TextView notfound_err;
    public static String request_phone, request_to, request_time;

    public static String donorBlood, uDivision, uDistrict, uThana;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_blood_requests);

        dialog = new AllDialog(this).showProgressDialog("", "Loading...", true, true);


        dialog.show();

        donorsList = (RecyclerView) findViewById(R.id.donorList);

        try {
            toolbar = (Toolbar) findViewById(R.id.app_bar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            SetToolbar.context = this;
            getSupportActionBar().setTitle(SetToolbar.setTitle());
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(SetToolbar.setBgColor()));
        } catch (Exception e) {

        }

        NavigationDrawerFragment drawerFragment =
                (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_drawer);
        drawerFragment.setUp(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        setRequestsList();

        DataLoader.checkLogin(this);
    }

    @Override
    public void onBackPressed() {
        try {
            startActivity(new Intent(this, AllSingleRequests.class));
            finish();
        } catch (Exception e) {

        }
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

    private void setRequestsList() {
//        Log.d("donorsByAddress",
//                " donorBlood "+ donorBlood+
//                        " uDivision "+ uDivision+
//                        " uDistrict "+ uDistrict+
//                        " uThana "+ uThana
//        );
        //Log.d("blood_requests",DataLoader.getUserPhone()+" "+AllBloodRequests.request_time);
        DataLoader.context = this;
        String url = getString(R.string.server_url) + "read_all_bloodrequests.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DataLoader.bloodRequests = new ArrayList<>();
//                Log.d("donorsByAddress",
//                        " response "+ response.toString()+
//                                " DataLoader.userDetails "+ DataLoader.userDetails.size()
//                );
                try {

                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject mJsonObject;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        mJsonObject = jsonArray.getJSONObject(i);

                        //insert userinfo into userDetails list arrays
                        DataLoader.BloodRequests requestInfo = new DataLoader.BloodRequests();
                        //pic_path,fname,lname,email,phone,birthDate,bloodgroup,location,latitude,longitude
                        requestInfo.request_phone = mJsonObject.getString("request_phone");
                        requestInfo.request_blood = mJsonObject.getString("request_blood");
                        requestInfo.request_address = mJsonObject.getString("request_address");
                        requestInfo.request_to = mJsonObject.getString("request_to");
                        requestInfo.request_time = mJsonObject.getString("request_time");
                        requestInfo.accepted = mJsonObject.getString("accepted");
                        requestInfo.deletion_date = mJsonObject.getString("deletion_date");

                        DataLoader.bloodRequests.add(requestInfo);

                    }

                    try {
                        dialog.hide();
                        donorListAdapter = new BloodRequestsListAdapter(AllBloodRequests.this, DataLoader.bloodRequests, getFragmentManager());
                        donorsList.setAdapter(donorListAdapter);
                        donorsList.setLayoutManager(new LinearLayoutManager(AllBloodRequests.this));

                        if (DataLoader.bloodRequests.size() == 0) {
                            new AllDialog(AllBloodRequests.this).showDialog("Not found", "Sorry no donor has accepted the request.", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(AllBloodRequests.this, BloodRequestOptions.class));
                                    finish();
                                }
                            }, "OK", null);

                        }
                    } catch (Exception e) {

                    }


                } catch (JSONException e) {

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    Toast.makeText(AllBloodRequests.this, "Network Error", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {

                }
                //Log.d("donorsByAddress", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("request_type", "allRequests");
                params.put("request_phone", DataLoader.getUserPhone());
                params.put("request_time", request_time);
                //params.put("request_to", request_to);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(this).addToRequest(stringRequest);


    }

}

