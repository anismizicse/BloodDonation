package com.ourdreamit.blooddonationproject.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ourdreamit.blooddonationproject.ui.adapters.AdminChatListAdapter;
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

public class AdminChatList extends AppCompatActivity {
    public RecyclerView donorsList;
    public AdminChatListAdapter chatListAdapter;
    private Toolbar toolbar;
    ProgressDialog dialog;

    private static final String TAG = "AdminChatList";

    TextView notfound_err;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_chat_list);

        donorsList = (RecyclerView) findViewById(R.id.donorList);
        notfound_err = (TextView) findViewById(R.id.notfound_err);

        dialog = new AllDialog(this).showProgressDialog("",
                "Loading Chat list...",true,true);
        dialog.show();


        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        SetToolbar.context = this;
        getSupportActionBar().setTitle(SetToolbar.setTitle());
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(SetToolbar.setBgColor()));

        NavigationDrawerFragment drawerFragment =
                (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_drawer);
        drawerFragment.setUp(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        setChatList();

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



    private void setChatList() {

        DataLoader.context = this;
        String url = getString(R.string.server_url) + "load_chatlist.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Log.d(TAG, "onResponse: "+response);
                
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
                        chatListInfo.donor_fcm_uid = mJsonObject.getString("donor_fcm_uid");
                        chatListInfo.admin_phone = mJsonObject.getString("admin_phone");
                        chatListInfo.pic_path = mJsonObject.getString("pic_path");
                        chatListInfo.name = mJsonObject.getString("name");
                        chatListInfo.address = mJsonObject.getString("address");
                        chatListInfo.lastmsgtime = mJsonObject.getString("lastmsgtime");


                        DataLoader.chatListDetails.add(chatListInfo);

                    }


                    dialog.hide();
                    chatListAdapter = new AdminChatListAdapter(AdminChatList.this, DataLoader.chatListDetails, getFragmentManager());
                    donorsList.setAdapter(chatListAdapter);
                    donorsList.setLayoutManager(new LinearLayoutManager(AdminChatList.this));

                } catch (JSONException e) {

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AdminChatList.this, "Couldn't load the list", Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("admin_phone", DataLoader.getUserPhone());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(this).addToRequest(stringRequest);


    }
}
