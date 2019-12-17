package com.ourdreamit.blooddonationproject.ui.fragments;

import android.app.ProgressDialog;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ourdreamit.blooddonationproject.ui.adapters.DoctorListAdapter;
import com.ourdreamit.blooddonationproject.utils.MySingleton;
import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.dialogs.AllDialog;
import com.ourdreamit.blooddonationproject.utils.DataLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DoctorList extends Fragment {
    public RecyclerView doctorList;
    public DoctorListAdapter doctorListAdapter;
    ProgressDialog dialog;
    TextView notfound_err;

    public static String specialfor, division, district, upazila;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.doctors_list, container, false);

        notfound_err = (TextView) view.findViewById(R.id.notfound_err);

        dialog = new AllDialog(getActivity()).showProgressDialog("",
                "Searching Doctors...",true,true);

        dialog.show();

        doctorList = (RecyclerView) view.findViewById(R.id.doctorList);
        setDoctorList();

        //new LoadDonorList().execute();

        return view;
    }

    private void setDoctorList() {

        DataLoader.context = getActivity();
        String url = getString(R.string.server_url) + "doctors_by_address.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DataLoader.doctorDetails =  new ArrayList<>();

                try {

                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject mJsonObject;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        mJsonObject = jsonArray.getJSONObject(i);

                        //insert userinfo into userDetails list arrays
                        DataLoader.DoctorInfo doctorInfo = new DataLoader.DoctorInfo();
                        //pic_path,fname,lname,email,phone,birthDate,bloodgroup,location,latitude,longitude
                        doctorInfo.id = mJsonObject.getString("id");
                        doctorInfo.name = mJsonObject.getString("name");
                        doctorInfo.designation = mJsonObject.getString("designation");
                        doctorInfo.hospital = mJsonObject.getString("hospital");
                        doctorInfo.speacilist = mJsonObject.getString("speacilist");
                        doctorInfo.division = mJsonObject.getString("division");
                        doctorInfo.district = mJsonObject.getString("district");
                        doctorInfo.upazila = mJsonObject.getString("upazila");
                        doctorInfo.phone = mJsonObject.getString("phone");
                        doctorInfo.email = mJsonObject.getString("email");
                        doctorInfo.gender = mJsonObject.getString("gender");
                        doctorInfo.profile_photo = mJsonObject.getString("profile_photo");
                        doctorInfo.preasent_address = mJsonObject.getString("preasent_address");

                        doctorInfo.doctor_detail = mJsonObject.getString("doctor_detail");
                        doctorInfo.chamber_address = mJsonObject.getString("chamber_address");


                        DataLoader.doctorDetails.add(doctorInfo);

                    }

                    try {
                        dialog.hide();
                        doctorListAdapter = new DoctorListAdapter(getActivity(), DataLoader.doctorDetails, getActivity().getFragmentManager());
                        doctorList.setAdapter(doctorListAdapter);
                        doctorList.setLayoutManager(new LinearLayoutManager(getActivity()));

                        if (DataLoader.doctorDetails.size() == 0) {
                            notfound_err.setVisibility(View.VISIBLE);
                        }
                    }catch (Exception e){

                    }

                } catch (JSONException e) {

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    Toast.makeText(getActivity(), "Network Error", Toast.LENGTH_SHORT).show();
                }catch (Exception e){

                }
                //Log.d("donorsByAddress", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("phone", DataLoader.getUserPhone());
                params.put("speacilist", specialfor);
                params.put("division", division);
                params.put("district", district);
                params.put("upazila", upazila);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(getActivity()).addToRequest(stringRequest);



    }


}

