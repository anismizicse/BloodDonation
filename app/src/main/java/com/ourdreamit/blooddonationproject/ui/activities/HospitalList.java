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
import com.ourdreamit.blooddonationproject.dialogs.AllDialog;
import com.ourdreamit.blooddonationproject.ui.adapters.HospitalListAdapter;
import com.ourdreamit.blooddonationproject.utils.MySingleton;
import com.ourdreamit.blooddonationproject.ui.fragments.NavigationDrawerFragment;
import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.utils.DataLoader;
import com.ourdreamit.blooddonationproject.utils.SetToolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HospitalList extends AppCompatActivity {
    public RecyclerView hospitalList;
    public HospitalListAdapter hospitalListAdapter;
    ProgressDialog dialog;
    private Toolbar toolbar;
    TextView notfound_err;
    public static String division, district, thana;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hospital_list);

        notfound_err = (TextView) findViewById(R.id.notfound_err);

        dialog = new AllDialog(this).showProgressDialog("",
                "Searching Hospitals...",true,true);


        dialog.show();

        hospitalList = (RecyclerView) findViewById(R.id.hospitalList);
        setHospitalList();

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

        DataLoader.checkLogin(this);

    }

    @Override
    public void onBackPressed() {
        try {
            Intent intent = new Intent(this, Search_Hospital.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }catch (Exception e){

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

    private void setHospitalList() {
//        Log.d("hospitals",
//                        " Division "+ division+
//                        " District "+ district+
//                        " Thana "+ thana
//        );
        String url = getString(R.string.server_url) + "get_hospitals.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DataLoader.hospitalDetails =  new ArrayList<>();
//                Log.d("hospitals",
//                        " response "+ response.toString()+
//                                " DataLoader.userDetails "+ DataLoader.hospitalDetails.size()
//                );
                try {

                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject mJsonObject;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        mJsonObject = jsonArray.getJSONObject(i);

                        //insert userinfo into userDetails list arrays
                        DataLoader.HospitalInfo hospitalInfo = new DataLoader.HospitalInfo();
                        //pic_path,fname,lname,email,phone,birthDate,bloodgroup,location,latitude,longitude
                        hospitalInfo.pic_path = mJsonObject.getString("pic_path");
                        hospitalInfo.name = mJsonObject.getString("name");
                        hospitalInfo.address = mJsonObject.getString("address");
                        hospitalInfo.speciality = mJsonObject.getString("speciality");
                        hospitalInfo.phone = mJsonObject.getString("phone");
                        hospitalInfo.latitude = mJsonObject.getString("latitude");
                        hospitalInfo.longitude = mJsonObject.getString("longitude");
                        hospitalInfo.division = mJsonObject.getString("division");
                        hospitalInfo.district = mJsonObject.getString("district");
                        hospitalInfo.thana = mJsonObject.getString("thana");


                        DataLoader.hospitalDetails.add(hospitalInfo);
//                        Log.d("hospitals",
//
//                                " DataLoader.userDetails "+ DataLoader.hospitalDetails.size()
//                        );
                        //DataLoader.userLatLng.add(latLng);

                        //Log.d("AllDonors",mJsonObject.toString());
                    }

//                    Log.d("donorsByAddress",
//
//                            " DataLoader.userDetails "+ DataLoader.hospitalDetails.size()
//                    );
                    try {
                        dialog.hide();
                        hospitalListAdapter = new HospitalListAdapter(HospitalList.this, DataLoader.hospitalDetails, getFragmentManager());
                        hospitalList.setAdapter(hospitalListAdapter);
                        hospitalList.setLayoutManager(new LinearLayoutManager(HospitalList.this));
                        if (DataLoader.hospitalDetails.size() == 0) {
                            notfound_err.setVisibility(View.VISIBLE);
                        }
                    }catch (Exception e){

                    }

                } catch (JSONException e) {
                    //e.printStackTrace();
//                    Log.d("donorsByAddress",
//
//                            " JSONException e "+ e.toString()
//                    );
                }
                //Log.d("AllDonors",response);
                //prepareNearbyPlaces(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    Toast.makeText(HospitalList.this, "Couldn't read url", Toast.LENGTH_SHORT).show();
                }catch (Exception e){

                }
                //Log.d("donorsByAddress", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("division", division);
                params.put("district", district);
                params.put("thana", thana);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(this).addToRequest(stringRequest);



    }
}
