package com.ourdreamit.blooddonationproject.ui.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.utils.SetToolbar;
import com.ourdreamit.blooddonationproject.dialogs.AllDialog;
import com.ourdreamit.blooddonationproject.ui.fragments.NavigationDrawerFragment;
import com.ourdreamit.blooddonationproject.utils.DataLoader;
import com.ourdreamit.blooddonationproject.utils.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Search_Hospital extends AppCompatActivity implements View.OnClickListener  {
    EditText division, district, upazila;
    String divisionList[], districtList[], upazilaList[];
    public String  uDivision = "Division*", uDistrict = "District*", uUpazila = "Upazila*";
    private Toolbar toolbar;

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_hospital);

        division = (EditText) findViewById(R.id.division);
        division.setInputType(InputType.TYPE_NULL);
        division.setOnClickListener(this);
        divisionList = getResources().getStringArray(R.array.divsion_list);

        district = (EditText) findViewById(R.id.district);
        district.setInputType(InputType.TYPE_NULL);
        district.setOnClickListener(this);

        upazila = (EditText) findViewById(R.id.upazila);
        upazila.setInputType(InputType.TYPE_NULL);
        upazila.setOnClickListener(this);

        progressDialog = new AllDialog(this).showProgressDialog("Loading...",
                getString(R.string.please_wait),true,true);

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
            Intent intent = new Intent(this, MainActivity.class);
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

    public void searchHospitals(View view){
        try {
            view.startAnimation(DataLoader.buttonClick);
            if (uDivision.equals("Division*") ||
                    uDistrict.equals("District*") || uUpazila.equals("Upazila*")) {
                new AllDialog(this).showDialog("", "Please fill all * marked fields.", null, null, "OK");


            } else {
                HospitalList.division = uDivision;
                HospitalList.district = uDistrict;
                HospitalList.thana = uUpazila;
                startActivity(new Intent(this, HospitalList.class));
            }
        }catch (Exception e){

        }
    }
    public void cancel(View view){
        view.startAnimation(DataLoader.buttonClick);
        startActivity(new Intent(this,MainActivity.class));
    }



    private void setDistrictList() {
        String app_server_url = getString(R.string.server_url) + "get_all_locations.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, app_server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progressDialog.dismiss();

                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject mJsonObject;
                            //profileInfo = new DataLoader.UserInfo();
                            districtList = new String[jsonArray.length()];
                            for (int i = 0; i < jsonArray.length(); i++) {
                                mJsonObject = jsonArray.getJSONObject(i);

                                districtList[i] = mJsonObject.getString("district");



                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            Toast.makeText(Search_Hospital.this, "Network Error", Toast.LENGTH_LONG).show();
                        }catch (Exception e){

                        }

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("phoneverify","true");
                params.put("division",uDivision);
                params.put("district", "na");
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(this).addToRequest(stringRequest);

        //district.setOnItemSelectedListener(this);
    }

    private void setUpazilaList() {
        String app_server_url = getString(R.string.server_url) + "get_all_locations.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, app_server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progressDialog.dismiss();

                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject mJsonObject;
                            //profileInfo = new DataLoader.UserInfo();
                            upazilaList = new String[jsonArray.length()];
                            for (int i = 0; i < jsonArray.length(); i++) {
                                mJsonObject = jsonArray.getJSONObject(i);

                                upazilaList[i] = mJsonObject.getString("thana");


                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            Toast.makeText(Search_Hospital.this, "Network Error", Toast.LENGTH_LONG).show();
                        }catch (Exception e){

                        }

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("phoneverify","true");
                params.put("division",uDivision);
                params.put("district", uDistrict);
                return params;
            }
        };
        MySingleton.getmInstance(this).addToRequest(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(this).addToRequest(stringRequest);

    }




    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.division:
                try {
                    new AllDialog(this).showListDialog("Division*", divisionList, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            division.setText(divisionList[which]);
                            uDivision = divisionList[which];

                            progressDialog.setMessage("Loading districts of " + uDivision);
                            progressDialog.show();
                            setDistrictList();
                        }
                    });
                }catch (Exception e){

                }
                break;
            case R.id.district:
                try {
                    new AllDialog(this).showListDialog("District*", districtList, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            district.setText(districtList[which]);
                            uDistrict = districtList[which];

                            progressDialog.setMessage("Loading upazillas of " + uDistrict);
                            progressDialog.show();
                            setUpazilaList();
                        }
                    });
                }catch (Exception e){

                }
                break;
            case R.id.upazila:
                try {
                    new AllDialog(this).showListDialog("Upazila*", upazilaList, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            upazila.setText(upazilaList[which]);
                            uUpazila = upazilaList[which];
                        }
                    });
                }catch (Exception e){

                }
                break;
        }

    }
}
