package com.ourdreamit.blooddonationproject.ui.fragments;

import android.app.ProgressDialog;
import androidx.fragment.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.Nullable;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.dialogs.AllDialog;
import com.ourdreamit.blooddonationproject.ui.activities.DonorsList;
import com.ourdreamit.blooddonationproject.ui.activities.MainActivity;
import com.ourdreamit.blooddonationproject.utils.DataLoader;
import com.ourdreamit.blooddonationproject.utils.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SearchByAddress extends Fragment implements View.OnClickListener,
        View.OnTouchListener {

    String divisionList[], districtList[], thanaList[];
    public String donorBlood = "Blood Group*", uDivision = "Division*", uDistrict = "District*", uThana = "Upazila*";
    AppCompatButton searchDonors, cancel;
    EditText bloodGroup, division, district, thana, blood_bags, hospital_name;

    ScrollView root;

    String[] bloodList;

    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_by_address, container, false);

        root = (ScrollView) view.findViewById(R.id.root);
        root.setOnTouchListener(this);

        blood_bags = (EditText) view.findViewById(R.id.blood_bags);
        hospital_name = (EditText) view.findViewById(R.id.hospital_name);

        bloodGroup = (EditText) view.findViewById(R.id.bloodGroup);
        bloodGroup.setInputType(InputType.TYPE_NULL);
        bloodGroup.setOnClickListener(this);

        bloodList = getResources().getStringArray(R.array.blood_group);

        division = (EditText) view.findViewById(R.id.division);
        division.setInputType(InputType.TYPE_NULL);
        division.setOnClickListener(this);
        division.setOnTouchListener(this);

        divisionList = getResources().getStringArray(R.array.divsion_list);

        district = (EditText) view.findViewById(R.id.district);
        district.setInputType(InputType.TYPE_NULL);
        district.setOnClickListener(this);
        district.setOnTouchListener(this);

        thana = (EditText) view.findViewById(R.id.thana);
        thana.setInputType(InputType.TYPE_NULL);
        thana.setOnClickListener(this);
        thana.setOnTouchListener(this);

        searchDonors = (AppCompatButton) view.findViewById(R.id.searchDonors);
        cancel = (AppCompatButton) view.findViewById(R.id.cancel);

        searchDonors.setOnClickListener(this);
        cancel.setOnClickListener(this);

        progressDialog = new AllDialog(getActivity()).showProgressDialog("Loading...",
                getString(R.string.please_wait), true, false);

        return view;
    }




    @Override
    public void onClick(View v) {
        //v.startAnimation(DataLoader.buttonClick);
        DataLoader.hideKeyboard(v, getActivity());
        int id = v.getId();
        switch (id) {
            case R.id.bloodGroup:
                try {
                    new AllDialog(getActivity()).showListDialog("Blood Group*", bloodList, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            bloodGroup.setText(bloodList[which]);
                            donorBlood = bloodList[which];
                        }
                    });
                }catch (Exception e){

                }
                break;
            case R.id.division:
                try {
                    new AllDialog(getActivity()).showListDialog("Division*", divisionList, new DialogInterface.OnClickListener() {
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
                    new AllDialog(getActivity()).showListDialog("District*", districtList, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            district.setText(districtList[which]);
                            uDistrict = districtList[which];
                            progressDialog.setMessage("Loading upazillas of " + uDistrict);
                            progressDialog.show();
                            setThanaList();
                        }
                    });
                }catch (Exception e){

                }
                break;
            case R.id.thana:
                try {
                    new AllDialog(getActivity()).showListDialog("Upazila*", thanaList, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            thana.setText(thanaList[which]);
                            uThana = thanaList[which];
                        }
                    });
                }catch (Exception e){

                }
                break;
            case R.id.searchDonors:
                try {
                    if (donorBlood.equals("Blood Group*") || blood_bags.getText().toString().isEmpty() ||
                            hospital_name.getText().toString().isEmpty() ||
                            uDivision.equals("Division*") ||
                            uDistrict.equals("District*") || uThana.equals("Upazila*")) {

                        new AllDialog(getActivity()).showDialog("", "Please fill all * marked fields.", null, null, "OK");


                    } else {
                        DonorsList.donorBlood = donorBlood;
                        DonorsList.blood_bags = blood_bags.getText().toString();
                        DonorsList.hospital = hospital_name.getText().toString();
                        DonorsList.uDivision = uDivision;
                        DonorsList.uDistrict = uDistrict;
                        DonorsList.uThana = uThana;
                        startActivity(new Intent(getActivity(), DonorsList.class));

                    }
                }catch (Exception e){

                }
                break;
            case R.id.cancel:
                try {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }catch (Exception e){

                }
                break;
        }
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
                            Toast.makeText(getActivity(), "Network Error", Toast.LENGTH_LONG).show();
                        }catch (Exception e){

                        }

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("division", uDivision);
                params.put("district", "na");
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(getActivity()).addToRequest(stringRequest);


    }

    private void setThanaList() {
        //Log.d("location",uDivision+" "+uDistrict);
        String app_server_url = getString(R.string.server_url) + "get_all_locations.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, app_server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.d("location",response.toString());
                        progressDialog.dismiss();

                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject mJsonObject;
                            //profileInfo = new DataLoader.UserInfo();
                            thanaList = new String[jsonArray.length()];
                            for (int i = 0; i < jsonArray.length(); i++) {
                                mJsonObject = jsonArray.getJSONObject(i);

                                thanaList[i] = mJsonObject.getString("thana");

                            }


                        } catch (JSONException e) {
                            //Log.d("location",e.toString());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            Toast.makeText(getActivity(), "Network Error", Toast.LENGTH_LONG).show();
                        }catch (Exception e){

                        }

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("division", uDivision);
                params.put("district", uDistrict);
                return params;
            }
        };
        MySingleton.getmInstance(getActivity()).addToRequest(stringRequest);


    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        try {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(v.getContext().INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
        }catch (Exception e){

        }
        return false;
    }
}
