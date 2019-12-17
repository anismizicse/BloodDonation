package com.ourdreamit.blooddonationproject.ui.fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
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
import com.ourdreamit.blooddonationproject.ui.adapters.DoctorChatListAdapter;
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

public class DoctorChatList extends Fragment {
    public RecyclerView donorsList;
    public DoctorChatListAdapter chatListAdapter;
    private Toolbar toolbar;
    ProgressDialog dialog;

    TextView notfound_err;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.doctor_chat_list, container, false);

        donorsList = (RecyclerView) view.findViewById(R.id.donorList);
        notfound_err = (TextView) view.findViewById(R.id.notfound_err);

        dialog = new AllDialog(getActivity()).showProgressDialog("",
                "Loading Doctor Chat List...",true,true);
        dialog.show();


        setChatList();

        return view;
    }





    private void setChatList() {

        DataLoader.context = getActivity();
        String url = getString(R.string.server_url) + "load_doctor_chatlist.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DataLoader.chatListDetails =  new ArrayList<>();

                try {

                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject mJsonObject;

                    if (jsonArray.length() == 0) {
                        notfound_err.setVisibility(View.VISIBLE);
                        donorsList.setVisibility(View.GONE);
                    }

                    for (int i = 0; i < jsonArray.length(); i++) {
                        mJsonObject = jsonArray.getJSONObject(i);

                        //insert userinfo into userDetails list arrays
                        DataLoader.AdminChatListInfo chatListInfo = new DataLoader.AdminChatListInfo();
                        //pic_path,fname,lname,email,phone,birthDate,bloodgroup,location,latitude,longitude
                        chatListInfo.id = mJsonObject.getString("id");
                        chatListInfo.donor_phone = mJsonObject.getString("donor_phone");
                        chatListInfo.admin_phone = mJsonObject.getString("doctor_phone");
                        chatListInfo.pic_path = mJsonObject.getString("pic_path");
                        chatListInfo.name = mJsonObject.getString("name");
                        chatListInfo.address = mJsonObject.getString("address");
                        chatListInfo.lastmsgtime = mJsonObject.getString("lastmsgtime");


                        DataLoader.chatListDetails.add(chatListInfo);

                    }


                    dialog.hide();
                    try {
                        chatListAdapter = new DoctorChatListAdapter(getActivity(), DataLoader.chatListDetails, getActivity().getFragmentManager());
                        donorsList.setAdapter(chatListAdapter);
                        donorsList.setLayoutManager(new LinearLayoutManager(getActivity()));
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
                //Log.d("donorsByAddress", error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("doctor_phone", DataLoader.getUserPhone());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(getActivity()).addToRequest(stringRequest);


    }
}
