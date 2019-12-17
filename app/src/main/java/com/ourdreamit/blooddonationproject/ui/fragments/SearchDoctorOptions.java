package com.ourdreamit.blooddonationproject.ui.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatButton;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.dialogs.AllDialog;
import com.ourdreamit.blooddonationproject.ui.activities.MainActivity;
import com.ourdreamit.blooddonationproject.utils.DataLoader;
import com.ourdreamit.blooddonationproject.utils.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SearchDoctorOptions extends Fragment implements View.OnClickListener {
    EditText speciality, division, district, upazila;
    String specialityList[],divisionList[],districtList[], upazilaList[];
    public String specialfor = "Speciality*", dDivision = "Division*", dDistrict = "District*", dUpazila = "Upazila*";
    AppCompatButton searchDoctors,cancel;
    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_doctor_options, null);

        speciality = (EditText) view.findViewById(R.id.speciality);
        speciality.setInputType(InputType.TYPE_NULL);
        speciality.setOnClickListener(this);


        division = (EditText) view.findViewById(R.id.division);
        division.setInputType(InputType.TYPE_NULL);
        division.setOnClickListener(this);
        divisionList = getResources().getStringArray(R.array.divsion_list);

        district = (EditText) view.findViewById(R.id.district);
        district.setInputType(InputType.TYPE_NULL);
        district.setOnClickListener(this);

        upazila = (EditText) view.findViewById(R.id.upazila);
        upazila.setInputType(InputType.TYPE_NULL);
        upazila.setOnClickListener(this);


        searchDoctors = (AppCompatButton) view.findViewById(R.id.searchDoctors);
        cancel = (AppCompatButton) view.findViewById(R.id.cancel);

        searchDoctors.setOnClickListener(this);
        cancel.setOnClickListener(this);

        progressDialog = new AllDialog(getActivity()).showProgressDialog("Loading Specialities...",
                getString(R.string.please_wait),true,true);

        setSpeciality();



        return view;
    }

    private void setSpeciality() {
        progressDialog.show();
        String app_server_url = getString(R.string.server_url) + "get_doctor_specialities.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, app_server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject mJsonObject;
                            //profileInfo = new DataLoader.UserInfo();
                            specialityList = new String[jsonArray.length()];
                            for (int i = 0; i < jsonArray.length(); i++) {
                                mJsonObject = jsonArray.getJSONObject(i);

                                specialityList[i] = mJsonObject.getString("name");

                            }
                            progressDialog.dismiss();


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
                });
        MySingleton.getmInstance(getActivity()).addToRequest(stringRequest);

        //speciality.setOnItemSelectedListener(this);
    }


    @Override
    public void onClick(View v) {
        v.startAnimation(DataLoader.buttonClick);
        int id = v.getId();
        switch (id) {
            case R.id.searchDoctors:
                try {
                    if (specialfor.equals("Speciality*") || dDivision.equals("Division*") ||
                            dDistrict.equals("District*") || dUpazila.equals("Upazila*")) {
                        new AllDialog(getActivity()).showDialog("", "Please fill all * marked fields.", null, null, "OK");


                    } else {
                        DoctorList.specialfor = specialfor;
                        DoctorList.division = dDivision;
                        DoctorList.district = dDistrict;
                        DoctorList.upazila = dUpazila;

                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.remove(fragmentManager.findFragmentByTag("doctorOptions"));
                        DoctorList fragment = new DoctorList();
                        fragmentTransaction.add(R.id.fragment_container, fragment, "doctorList");
                        fragmentTransaction.commit();
                    }
                }catch (Exception e){

                }
                break;
            case R.id.speciality:
                try {
                    new AllDialog(getActivity()).showListDialog("Speciality*", specialityList, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            speciality.setText(specialityList[which]);
                            specialfor = specialityList[which];
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
                            dDivision = divisionList[which];
                            progressDialog.setMessage("Loading districts of " + dDivision);
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
                            dDistrict = districtList[which];
                            progressDialog.setMessage("Loading upazillas of " + dDistrict);
                            progressDialog.show();
                            setUpazilaList();
                        }
                    });
                }catch ( Exception e){

                }
                break;
            case R.id.upazila:
                try {
                    new AllDialog(getActivity()).showListDialog("Upazila*", upazilaList, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            upazila.setText(upazilaList[which]);
                            dUpazila = upazilaList[which];
                        }
                    });
                }catch (Exception e){

                }
                break;
            case R.id.cancel:
                try {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
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
                //params.put("phoneverify","true");
                params.put("division",dDivision);
                params.put("district", "na");
                return params;
            }
        };
        MySingleton.getmInstance(getActivity()).addToRequest(stringRequest);

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
                            Toast.makeText(getActivity(), "Network Error", Toast.LENGTH_LONG).show();
                        }catch (Exception e){

                        }

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("phoneverify","true");
                params.put("division",dDivision);
                params.put("district", dDistrict);
                return params;
            }
        };
        MySingleton.getmInstance(getActivity()).addToRequest(stringRequest);


    }
}
