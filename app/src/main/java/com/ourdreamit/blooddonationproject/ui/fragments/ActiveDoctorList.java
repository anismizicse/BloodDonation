package com.ourdreamit.blooddonationproject.ui.fragments;

import android.app.ProgressDialog;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ourdreamit.blooddonationproject.utils.DataLoader;
import com.ourdreamit.blooddonationproject.utils.MySingleton;
import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.dialogs.AllDialog;
import com.ourdreamit.blooddonationproject.ui.adapters.ActiveDoctorListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActiveDoctorList extends Fragment {
    public RecyclerView doctorList;
    public ActiveDoctorListAdapter adminListAdapter;
    private Toolbar toolbar;
    ProgressDialog dialog;

    TextView notfound_err;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.active_doctorlist, container, false);
        doctorList = (RecyclerView) view.findViewById(R.id.doctorList);
        notfound_err = (TextView) view.findViewById(R.id.notfound_err);

        dialog = new AllDialog(getActivity()).showProgressDialog("", "Loading doctors list...", true, true);
        dialog.show();

        setActiveDoctorList();

        return view;
    }



    private void setActiveDoctorList() {

        String url = getString(R.string.server_url) + "get_all_active_doctors.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //if()
                DataLoader.activeDoctorDetails =  new ArrayList<>();

                try {

                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject mJsonObject;

                    if (jsonArray.length() == 0) {
                        notfound_err.setVisibility(View.VISIBLE);
                        doctorList.setVisibility(View.GONE);
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


                        DataLoader.activeDoctorDetails.add(userInfo);

                    }

                    try {
                        dialog.hide();
                        adminListAdapter = new ActiveDoctorListAdapter(getActivity(), DataLoader.activeDoctorDetails, getActivity().getFragmentManager());
                        doctorList.setAdapter(adminListAdapter);
                        doctorList.setLayoutManager(new LinearLayoutManager(getActivity()));
                    }catch (Exception e){

                    }


                } catch (JSONException e) {

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    Toast.makeText(getActivity(), "Couldn't load the list", Toast.LENGTH_SHORT).show();
                }catch (Exception e){

                }
                //Log.d("get_all_admins", error.toString());
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(getActivity()).addToRequest(stringRequest);


    }
}
