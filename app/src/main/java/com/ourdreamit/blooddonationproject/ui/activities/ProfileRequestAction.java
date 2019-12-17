package com.ourdreamit.blooddonationproject.ui.activities;

import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.dialogs.AllDialog;
import com.ourdreamit.blooddonationproject.fcm.FcmNotificationBuilder;
import com.ourdreamit.blooddonationproject.utils.Constants;
import com.ourdreamit.blooddonationproject.utils.DataLoader;
import com.ourdreamit.blooddonationproject.utils.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileRequestAction extends AppCompatActivity {
    public Intent intent;
    String senderName, message;
    TextView name,phone,blood_group,bags,hospitalName,request_address;
    public static String receiverToken, request_from, uid, requested_blood, hospital,blood_bags,requester_address;
    public static Context context;
    static FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_request_action);

        name = (TextView) findViewById(R.id.name);
        phone = (TextView) findViewById(R.id.phone);
        blood_group = (TextView) findViewById(R.id.blood_group);
        bags = (TextView) findViewById(R.id.bags);
        hospitalName = (TextView) findViewById(R.id.hospital);
        request_address = (TextView) findViewById(R.id.request_from);

        if (getIntent().getBooleanExtra("Exit me", false)) {
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }

        context = this;
        manager = getFragmentManager();

        //request_msg = (TextView) findViewById(R.id.request_msg);

        intent = getIntent();

        if (intent.hasExtra(Constants.ARG_RECEIVER_UID)) {
            senderName = intent.getStringExtra(Constants.ARG_RECEIVER);
            uid = intent.getStringExtra(Constants.ARG_RECEIVER_UID);
            message = intent.getStringExtra(Constants.MESSAGE);
            //Log.d("FirebaseProfileDetails", senderName);
            receiverToken = intent.getStringExtra(Constants.ARG_FIREBASE_TOKEN);
            //Log.d("FirebaseProfileDetails",receiverToken);


//        if (intent.hasExtra(Constants.ARG_FIREBASE_TOKEN)) {
//
//        }
            name.setText(senderName);
            //Log.d("receiver",message.toString());
            try {
                JSONObject mJsonObject = new JSONObject(message);
                phone.setText(mJsonObject.getString("requester_phone"));
                blood_group.setText(mJsonObject.getString("requested_blood"));
                blood_bags = mJsonObject.getString("bags");
                bags.setText(blood_bags);
                hospital = mJsonObject.getString("hospital");
                hospitalName.setText(hospital);
                request_address.setText(mJsonObject.getString("address"));
            } catch (JSONException e) {
                e.printStackTrace();
                //Log.d("receiver", e.toString());
            }

            DataLoader.checkLogin(this);
        }

        /*DataLoader.context = this;
        if(!DataLoader.checkInternet()){

            new AlertDialog.Builder(context, R.style.MyAlertDialogStyle)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Connection Error")
                    .setMessage("Please connect internet.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent reload = new Intent(ProfileRequestAction.this, ProfileRequestAction.class);
                            reload.putExtra(Constants.ARG_RECEIVER, senderName);
                            reload.putExtra(Constants.ARG_RECEIVER_UID, uid);
                            reload.putExtra(Constants.MESSAGE, message);
                            reload.putExtra(Constants.ARG_FIREBASE_TOKEN, receiverToken);

                        }

                    }).show();

        }else{
            new AlertDialog.Builder(context, R.style.MyAlertDialogStyle)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Loading Error")
                    .setMessage("Sorry the request couldn't be loaded.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Intent intent = new Intent(context, ProfileRequestAction.class);
                            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("Exit me", true);
                            context.startActivity(intent);
                        }

                    }).show();
        }*/

        //request_msg.setText(senderName + " needs Blood and wants to call you. " +
               // "If you accept, the requester will be able to contact you. If you cancel your profile will be hidden for 1 day.");

    }

    public void accept(View view) {
        //view.startAnimation(DataLoader.buttonClick);

        new AllDialog(context).showDialog("Blood Request", "Are you sure to accept this request?",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DataLoader.context = ProfileRequestAction.this;
                        DataLoader.profileInfo = null;
                        DataLoader.getUserFromServer("ProfileRequestAction");
                    }
                },"Yes","No");

        /*new AlertDialog.Builder(context, R.style.MyAlertDialogStyle)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Blood Request")
                .setMessage("Are you sure to accept this request?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DataLoader.context = ProfileRequestAction.this;
                        DataLoader.profileInfo = null;
                        DataLoader.getUserFromServer("ProfileRequestAction");

                    }

                })
                .setNegativeButton("No", null)
                .show();*/

    }

    public void cancel(View view) {
        //view.startAnimation(DataLoader.buttonClick);
        new AllDialog(context).showDialog("Alert !", "Your profile will be hidden for 1 day. Do you want to continue?",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        hideProfile();

                    }
                },"Yes","No");

//        CancelBloodRequest cancel = new CancelBloodRequest();
//        cancel.show(manager, "cancelBloodRequest");
        //finish();
    }

    public static void hideProfile() {
        //Gets todays date(day,month,year)
        final Calendar cal = Calendar.getInstance();
        int yy = cal.get(Calendar.YEAR);
        int mm = 1 + cal.get(Calendar.MONTH);
        int dd = cal.get(Calendar.DAY_OF_MONTH);
        String dateInString = dd + "-" + mm + "-" + yy;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(dateInString));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, 1);
        sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date resultdate = new Date(c.getTimeInMillis());
        dateInString = sdf.format(resultdate);
        final String autopro_visible = dateInString;

        String app_server_url = context.getString(R.string.server_url) + "hide_profile_oneday.php";
        //Toast.makeText(AllStatic.ctx, "inside updateDatabase", Toast.LENGTH_LONG).show();
        //Log.d("updateLat","phone "+getUserPhone()+" latitude "+ " "+latitude+"longitude"+ " "+longitude+" address "+address);
        //Log.d("autopro_visible", autopro_visible);
        try {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, app_server_url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //Toast.makeText(context, response, Toast.LENGTH_LONG).show();
                            if (response.equals("updated")) {
                                Toast.makeText(context, "Your profile has been hidden for 1 day", Toast.LENGTH_LONG).show();


                                Intent intent = new Intent(context, ProfileRequestAction.class);
                                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("Exit me", true);
                                context.startActivity(intent);


                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //Log.d("insert_blood_requests", error.toString());
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("phone", DataLoader.getUserPhone());
                    params.put("autopro_visible", autopro_visible);

                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MySingleton.getmInstance(context).addToRequest(stringRequest);
        } catch (Exception e) {
            Toast.makeText(context, "" + e, Toast.LENGTH_LONG).show();
        }
    }

    public static void sendRequest() {

        String username = DataLoader.profileInfo.fname + " " + DataLoader.profileInfo.lname;
        String message = username + " has accepted your request. You can call this donor now";
        String uid = DataLoader.profileInfo.fcm_uid;
        String firebaseToken = DataLoader.profileInfo.fcm_token;
        String receiverFirebaseToken = receiverToken;

        FcmNotificationBuilder.initialize()
                .title("Call Request Accepted")
                .message(message)
                .username(username)
                .uid(uid)
                .firebaseToken(firebaseToken)
                .receiverFirebaseToken(receiverFirebaseToken)
                .send();
        getSenderDetails();
        //acceptedRequest();

    }

    private static void getSenderDetails() {
        //Log.d("profileRequestAction", uid + " " + DataLoader.getUserPhone() + " " + DataLoader.getUserPass() + " " + DataLoader.getUserType() + " " + DataLoader.getUserBlood());
        String app_server_url = context.getString(R.string.server_url) + "get_requester_details.php";
        //Toast.makeText(AllStatic.ctx, "inside updateDatabase", Toast.LENGTH_LONG).show();
        //Log.d("updateLat","phone "+getUserPhone()+" latitude "+ " "+latitude+"longitude"+ " "+longitude+" address "+address);
        try {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, app_server_url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {

                                JSONArray jsonArray = new JSONArray(response);
                                JSONObject mJsonObject;

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    mJsonObject = jsonArray.getJSONObject(i);

                                    //insert userinfo into userDetails list arrays

                                    //pic_path,fname,lname,email,phone,birthDate,bloodgroup,location,latitude,longitude
                                    request_from = mJsonObject.getString("phone");
                                    requester_address = mJsonObject.getString("address");


                                }
                                acceptedRequest();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //Log.d("insert_blood_requests", error.toString());
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("fcm_uid", uid);
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MySingleton.getmInstance(context).addToRequest(stringRequest);
        } catch (Exception e) {
            Toast.makeText(context, "" + e, Toast.LENGTH_LONG).show();
        }
    }

    private static void acceptedRequest() {
        DataLoader.context = context;
        //Log.d("profileRequestAction", request_from + " " + DataLoader.getUserBlood() + " " + requester_address + " " + DataLoader.getUserPhone());
        String app_server_url = context.getString(R.string.server_url) + "insert_accepted_blood_requests.php";
        //Toast.makeText(AllStatic.ctx, "inside updateDatabase", Toast.LENGTH_LONG).show();
//        Log.d("updateLat","request_from "+request_from+" request_blood "+ " "+DataLoader.getUserBlood()+
//                "request_address"+ " "+request_address+" request_to "+DataLoader.getUserPhone());
        try {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, app_server_url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //Toast.makeText(context, response, Toast.LENGTH_LONG).show();
                            if (response.equals("success")) {
                                //Toast.makeText(context, response, Toast.LENGTH_LONG).show();
                                new AllDialog(context).showDialog("Blood Request", "Success ! the requester has been notified.",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent intent = new Intent(context, ProfileRequestAction.class);
                                                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                intent.putExtra("Exit me", true);
                                                context.startActivity(intent);

                                            }
                                        },"OK",null);
                                /*new AlertDialog.Builder(context, R.style.MyAlertDialogStyle)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setTitle("Blood Request")
                                        .setMessage("Success ! the requester has been notified.")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(context, ProfileRequestAction.class);
                                                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                intent.putExtra("Exit me", true);
                                                context.startActivity(intent);
                                            }

                                        }).show();*/
                            }else if(response.equals("managed")){
                                new AllDialog(context).showDialog("Blood Request", "Sorry ! the requester has already managed the blood. Thank you" +
                                                " for your cooperation.",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent intent = new Intent(context, ProfileRequestAction.class);
                                                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                intent.putExtra("Exit me", true);
                                                context.startActivity(intent);

                                            }
                                        },"OK",null);
                                /*new AlertDialog.Builder(context, R.style.MyAlertDialogStyle)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setTitle("Blood Request")
                                        .setMessage("Sorry ! the requester has already managed the blood. Thank you" +
                                                " for your cooperation.")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(context, ProfileRequestAction.class);
                                                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                intent.putExtra("Exit me", true);
                                                context.startActivity(intent);
                                            }

                                        }).show();*/
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //Log.d("insert_blood_requests", error.toString());
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("request_phone", request_from);
                    params.put("request_blood", DataLoader.getUserBlood());
                    //params.put("request_address", request_address);
                    params.put("accepted_phone", DataLoader.getUserPhone());

                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MySingleton.getmInstance(context).addToRequest(stringRequest);
        } catch (Exception e) {
            Toast.makeText(context, "" + e, Toast.LENGTH_LONG).show();
        }
    }

    private static void finishActivity() {

    }


}
