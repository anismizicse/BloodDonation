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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ourdreamit.blooddonationproject.utils.DataLoader;
import com.ourdreamit.blooddonationproject.utils.MySingleton;
import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.ui.adapters.SingleRequestsListAdapter;
import com.ourdreamit.blooddonationproject.dialogs.AllDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AllSingleRequests extends Fragment {
    public RecyclerView singleRequests;
    public SingleRequestsListAdapter singleRequestsListAdapter;
    ProgressDialog dialog;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all_single_requests, container, false);
        dialog = new AllDialog(getActivity()).showProgressDialog("","Loading...",true,true);
        singleRequests = (RecyclerView) view.findViewById(R.id.singleRequests);
        setDonorList();
        return view;
    }



    private void setDonorList() {

        DataLoader.context = getActivity();
        String url = getString(R.string.server_url) + "read_all_bloodrequests.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DataLoader.bloodRequests =  new ArrayList<>();

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
                        singleRequestsListAdapter = new SingleRequestsListAdapter(getActivity(), DataLoader.bloodRequests, getActivity().getFragmentManager());
                        singleRequests.setAdapter(singleRequestsListAdapter);
                        singleRequests.setLayoutManager(new LinearLayoutManager(getActivity()));
                    }catch (Exception e){

                    }



                } catch (JSONException e) {

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    Toast.makeText(getActivity(), "Couldn't read url", Toast.LENGTH_SHORT).show();
                }catch (Exception e){

                }
                //Log.d("donorsByAddress", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("request_type", "singleRequest");
                params.put("request_phone", DataLoader.getUserPhone());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(getActivity()).addToRequest(stringRequest);



    }

}

