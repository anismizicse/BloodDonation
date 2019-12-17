package com.ourdreamit.blooddonationproject.utils;

import android.app.FragmentManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.core.app.NotificationCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.ourdreamit.blooddonationproject.ui.activities.MainActivity;
import com.ourdreamit.blooddonationproject.ui.adapters.DonorListAdapter;
import com.ourdreamit.blooddonationproject.ui.activities.EditProfileDetails;
import com.ourdreamit.blooddonationproject.ui.activities.Login;
import com.ourdreamit.blooddonationproject.ui.activities.MyStatus;
import com.ourdreamit.blooddonationproject.ui.activities.ProfileDetails;
import com.ourdreamit.blooddonationproject.ui.activities.ProfileRequestAction;
import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.ui.fragments.SearchDonorFragment;
import com.ourdreamit.blooddonationproject.dialogs.DonorBasicInfo;
import com.ourdreamit.blooddonationproject.ui.activities.BasicInformation;
import com.ourdreamit.blooddonationproject.ui.activities.SplashActivity;
import com.ourdreamit.blooddonationproject.ui.adapters.AdminListAdapter;
import com.ourdreamit.blooddonationproject.ui.fragments.LoginFragment;
import com.ourdreamit.blooddonationproject.ui.fragments.RegisterFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by anismizi on 6/20/17.
 */

public class DataLoader extends AsyncTask<Void, Void, Void> {
    public static Context context;
    public static final String APP_PREFS = "application_preferences";
    public static final String FCM_TOKEN = "fcm_token";
    public static final String PHONE_NUMBER = "phone";
    public static final String PASSWORD = "password";
    public static final String USERTYPE = "usertype";
    public static final String BLOODGROUP = "bloodgroup";
    public static final String LOGIN = "login";
    public static final String LOGOUT = "logout";
    public static final String ADMINCHATLOGIN = "adminchatlogin";
    public static final String NOTIFICATIONID = "notificationid";
    public static final String RECENTCHAT = "recentchat";
    public static final String PROCHECKDATE = "procheckdate";
    public static final String HOTLINE = "hotline";
    public static final String AMBULANCE = "ambulance";
    public static final String LIVECHATLOGIN = "livechatlogin";

    public static Boolean doctorCarePanel, donorToDoctorChat, doctorToDonorChat = false;
    public static Boolean tokenChanged = false;
    //NavigationDrawerFragment Static variables
    public static DrawerLayout mDrawerLayout;

    public static AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);

    public static Boolean isAdminChatting = false;
    public static Boolean isDoctorChatting = false;

    public static String getUserPhone() {
        if (userPhone == null) {
            //setUserFromLocal();
            userPhone = new SharedPrefUtil(context).getString(PHONE_NUMBER, null);
        }
        return userPhone;
    }

    public static String getUserPass() {
        if (userPass == null) {
            //setUserFromLocal();
            userPass = new SharedPrefUtil(context).getString(PASSWORD, null);
        }
        return userPass;
    }

    public static String getUserBlood() {
        if (blood_group == null) {
            //setUserFromLocal();
            blood_group = new SharedPrefUtil(context).getString(BLOODGROUP, null);
        }
        return blood_group;
    }


    public static String userPhone;
    public static String userPass;
    public static String userType;
    public static String blood_group;


    public static String livechatlogin;

    public static String getHotline() {
        if (hotline == null) {
            hotline = new SharedPrefUtil(context).getString(HOTLINE, "na");
        }
        return hotline;
    }

    public static String hotline;

    public static String getAmbulane() {
        if (ambulane == null) {
            ambulane = new SharedPrefUtil(context).getString(AMBULANCE, "na");
        }
        return ambulane;
    }

    public static String ambulane;

    public static String getUserType() {
        if (userType == null) {
            userType = new SharedPrefUtil(context).getString(USERTYPE, "na");
        }

        return userType;
    }

    public static UserInfo getUserFromServer(String requestFrom) {
        if (profileInfo == null) {
            setUserFromServer(requestFrom);
        }
        return profileInfo;
    }

    public interface TalkToProfileDetails {
        void setUserDetails();
    }

    private static void setUserFromServer(final String requestFrom) {
        String app_server_url = context.getString(R.string.server_url) + "getcurrentuser.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, app_server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject mJsonObject;
                            profileInfo = new UserInfo();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                mJsonObject = jsonArray.getJSONObject(i);

                                profileInfo.phone = mJsonObject.getString("phone");
                                profileInfo.password = mJsonObject.getString("password");
                                profileInfo.pic_path = mJsonObject.getString("pic_path");
                                profileInfo.fname = mJsonObject.getString("fname");
                                profileInfo.lname = mJsonObject.getString("lname");
                                profileInfo.blood_group = mJsonObject.getString("blood_group");
                                profileInfo.birth_date = mJsonObject.getString("birth_date");
                                profileInfo.age = mJsonObject.getString("age");
                                profileInfo.last_donation = mJsonObject.getString("last_donation");
                                profileInfo.new_donor = mJsonObject.getString("new_donor");
                                profileInfo.email = mJsonObject.getString("email");
                                profileInfo.division = mJsonObject.getString("division");
                                profileInfo.district = mJsonObject.getString("district");
                                profileInfo.upazila = mJsonObject.getString("upazila");

                                profileInfo.address = mJsonObject.getString("address");

                                if (mJsonObject.getString("latitude").equals("null") ||
                                        mJsonObject.getString("latitude").equals("na") ||
                                        mJsonObject.getString("longitude").equals("null") ||
                                        mJsonObject.getString("longitude").equals("na")) {

                                    profileInfo.latitude = 0.0;
                                    profileInfo.longitude = 0.0;

                                } else {
                                    try {
                                        profileInfo.latitude = Double.parseDouble(mJsonObject.getString("latitude"));
                                        profileInfo.longitude = Double.parseDouble(mJsonObject.getString("longitude"));
                                    } catch (Exception e) {

                                    }
                                }

                                profileInfo.code = mJsonObject.getString("code");
                                profileInfo.verification = mJsonObject.getString("verification");

                                if (mJsonObject.getString("lastLat").equals("null") ||
                                        mJsonObject.getString("lastLat").equals("na") ||
                                        mJsonObject.getString("lastLng").equals("null") ||
                                        mJsonObject.getString("lastLng").equals("na")) {

                                    profileInfo.lastLat = 0.0;
                                    profileInfo.lastLng = 0.0;

                                } else {
                                    try {
                                        profileInfo.lastLat = Double.parseDouble(mJsonObject.getString("lastLat"));
                                        profileInfo.lastLng = Double.parseDouble(mJsonObject.getString("lastLng"));
                                    } catch (Exception e) {

                                    }
                                }


                                profileInfo.fcm_email = mJsonObject.getString("fcm_email");
                                profileInfo.fcm_uid = mJsonObject.getString("fcm_uid");
                                profileInfo.fcm_token = mJsonObject.getString("fcm_token");


                                profileInfo.pro_visible = mJsonObject.getString("pro_visible");
                                profileInfo.called_date = mJsonObject.getString("called_date");
                                profileInfo.called_today = mJsonObject.getString("called_today");
                                profileInfo.donations_number = mJsonObject.getString("donations_number");
                                profileInfo.user_type = mJsonObject.getString("user_type");
                                profileInfo.gender = mJsonObject.getString("gender");
                                profileInfo.already_donated = mJsonObject.getString("already_donated");
                                profileInfo.autopro_visible = mJsonObject.getString("autopro_visible");
                                profileInfo.singup_steps = mJsonObject.getString("singup_steps");

                                profileInfo.post_code = mJsonObject.getString("post_code");
                                profileInfo.rank = mJsonObject.getString("rank");
                                profileInfo.web_url = mJsonObject.getString("web_url");
                                profileInfo.fb_url = mJsonObject.getString("fb_url");
                                profileInfo.religion = mJsonObject.getString("religion");
                                profileInfo.is_physically_disble = mJsonObject.getString("is_physically_disble");
                                profileInfo.nationality = mJsonObject.getString("nationality");
                                profileInfo.nid = mJsonObject.getString("nid");
                                profileInfo.status = mJsonObject.getString("status");

                                if (requestFrom.equals("MainActivityFcmCheck")) {
                                    try {
                                        String local_Token = new SharedPrefUtil(context).getString(DataLoader.FCM_TOKEN, null);
                                        if (local_Token != null && !profileInfo.fcm_token.equals(local_Token)) {

                                            DataLoader.tokenChanged = true;
                                            DataLoader.changedTokenLogout(context);

                                        }
                                    } catch (Exception e) {

                                    }
                                }

                                if (requestFrom.equals("ProfileDetails")) {
                                    try {
                                        TalkToProfileDetails talk;
                                        talk = (ProfileDetails) context;
                                        talk.setUserDetails();
                                    }catch (Exception e){

                                    }
                                }

                                if (requestFrom.equals("MyStatus")) {
                                    try {
                                        TalkToProfileDetails talk;
                                        talk = (MyStatus) context;
                                        talk.setUserDetails();
                                    }catch (Exception e){

                                    }
                                }

                                if (requestFrom.equals("LoginFragment")) {
                                    try {
                                        LoginFragment.logintoChat();
                                    }catch (Exception e){

                                    }
                                }

                                if (requestFrom.equals("DonorBasicInfo")) {
                                    try {
                                        DonorBasicInfo.sendRequest();
                                    }catch (Exception e){

                                    }
                                }


                                if (requestFrom.equals("ProfileRequestAction")) {
                                    try {
                                        ProfileRequestAction.sendRequest();
                                    }catch (Exception e){

                                    }
                                }

                                if (requestFrom.equals("DonorListAdapter")) {

                                    try {
                                        String local_Token = new SharedPrefUtil(context).getString(DataLoader.FCM_TOKEN, null);
                                        if (local_Token != null && !profileInfo.fcm_token.equals(local_Token)) {

                                            DataLoader.changedTokenLogout(context);

                                        } else {

                                            DonorListAdapter.updateBloodRequest();

                                        }
                                    }catch (Exception e){

                                    }

                                }

                                if (requestFrom.equals("EditProfileDetails")) {
                                    try {
                                        ProfileDetails.dialog.hide();
                                        context.startActivity(new Intent(context, EditProfileDetails.class));
                                    }catch (Exception e){

                                    }

                                }

                                if (requestFrom.equals("insertAdminChatList")) {
                                    try {
                                        insertAdminChatList();
                                    }catch (Exception e){

                                    }
                                }
                                if (requestFrom.equals("AdminListAdapter")) {
                                    try {
                                        String local_Token = new SharedPrefUtil(context).getString(DataLoader.FCM_TOKEN, null);
                                        if (local_Token != null && !profileInfo.fcm_token.equals(local_Token)) {

                                            //DataLoader.changedTokenLogout(context);
                                            AdminListAdapter.dialog.dismiss();
                                            tokenChanged = true;

                                        } else {
                                            insertAdminChatList();
                                            AdminListAdapter.dialog.dismiss();
                                        }
                                    }catch (Exception e){

                                    }
                                }
                                if (requestFrom.equals("ActiveDoctorListAdapter")) {
                                    try {
                                        String local_Token = new SharedPrefUtil(context).getString(DataLoader.FCM_TOKEN, null);
                                        if (local_Token != null && !profileInfo.fcm_token.equals(local_Token)) {

                                            //DataLoader.changedTokenLogout(context);
                                            tokenChanged = true;

                                        } else {

                                            insertDoctorChatList();

                                        }
                                    }catch (Exception e){

                                    }

                                }

                                if (requestFrom.equals("BloodRequest")) {
                                    try {
                                        String local_Token = new SharedPrefUtil(context).getString(DataLoader.FCM_TOKEN, null);
                                        if (local_Token != null && !profileInfo.fcm_token.equals(local_Token)) {

                                            DataLoader.changedTokenLogout(context);

                                        } else {

                                            SearchDonorFragment.updateBloodRequest();

                                        }
                                    }catch (Exception e){

                                    }
                                }


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (requestFrom.equals("EditProfileDetails")) {
                            ProfileDetails.dialog.hide();
                        }
                        Toast.makeText(context, "Network Error", Toast.LENGTH_LONG).show();
                        //Log.d("myStatus",error.toString());

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("phoneverify","true");
                params.put("phone", "" + getUserPhone());
                params.put("password", getUserPass());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(context).addToRequest(stringRequest);
    }

    public static UserInfo profileInfo;


    public static UserInfo getAdminInfo() {
        if (adminInfo == null) {
            setAdminInfo();
        }
        return adminInfo;
    }

    public static UserInfo adminInfo;

    private static void setAdminInfo() {
        //Log.d("AdminInfo",context.getString(R.string.admin_phone)+" "+context.getString(R.string.admin_pass));
        String app_server_url = context.getString(R.string.server_url) + "getcurrentuser.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, app_server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject mJsonObject;
                            adminInfo = new UserInfo();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                mJsonObject = jsonArray.getJSONObject(i);

                                //insert userinfo into userDetails list arrays

                                //pic_path,fname,lname,email,phone,birthDate,bloodgroup,location,latitude,longitude
                                adminInfo.phone = mJsonObject.getString("phone");
                                adminInfo.password = mJsonObject.getString("password");
                                adminInfo.pic_path = mJsonObject.getString("pic_path");
                                adminInfo.fname = mJsonObject.getString("fname");
                                adminInfo.lname = mJsonObject.getString("lname");
                                adminInfo.blood_group = mJsonObject.getString("blood_group");
                                adminInfo.birth_date = mJsonObject.getString("birth_date");
                                adminInfo.age = mJsonObject.getString("age");
                                adminInfo.last_donation = mJsonObject.getString("last_donation");
                                adminInfo.new_donor = mJsonObject.getString("new_donor");
                                adminInfo.email = mJsonObject.getString("email");
                                adminInfo.division = mJsonObject.getString("division");
                                adminInfo.district = mJsonObject.getString("district");
                                adminInfo.upazila = mJsonObject.getString("upazila");

                                adminInfo.address = mJsonObject.getString("address");

                                if (mJsonObject.getString("latitude").equals("null") ||
                                        mJsonObject.getString("latitude").equals("na") ||
                                        mJsonObject.getString("longitude").equals("null") ||
                                        mJsonObject.getString("longitude").equals("na")) {

                                    adminInfo.latitude = 0.0;
                                    adminInfo.longitude = 0.0;

                                } else {
                                    try {
                                        adminInfo.latitude = Double.parseDouble(mJsonObject.getString("latitude"));
                                        adminInfo.longitude = Double.parseDouble(mJsonObject.getString("longitude"));
                                    } catch (Exception e) {

                                    }
                                }

                                adminInfo.code = mJsonObject.getString("code");
                                adminInfo.verification = mJsonObject.getString("verification");

                                if (mJsonObject.getString("lastLat").equals("null") ||
                                        mJsonObject.getString("lastLat").equals("na") ||
                                        mJsonObject.getString("lastLng").equals("null") ||
                                        mJsonObject.getString("lastLng").equals("na")) {

                                    adminInfo.lastLat = 0.0;
                                    adminInfo.lastLng = 0.0;

                                } else {
                                    try {
                                        adminInfo.lastLat = Double.parseDouble(mJsonObject.getString("lastLat"));
                                        adminInfo.lastLng = Double.parseDouble(mJsonObject.getString("lastLng"));
                                    } catch (Exception e) {

                                    }
                                }

                                adminInfo.fcm_email = mJsonObject.getString("fcm_email");
                                adminInfo.fcm_uid = mJsonObject.getString("fcm_uid");
                                adminInfo.fcm_token = mJsonObject.getString("fcm_token");


                                adminInfo.pro_visible = mJsonObject.getString("pro_visible");
                                adminInfo.called_date = mJsonObject.getString("called_date");
                                adminInfo.called_today = mJsonObject.getString("called_today");
                                adminInfo.donations_number = mJsonObject.getString("donations_number");
                                adminInfo.user_type = mJsonObject.getString("user_type");
                                adminInfo.gender = mJsonObject.getString("gender");
                                adminInfo.already_donated = mJsonObject.getString("already_donated");
                                adminInfo.autopro_visible = mJsonObject.getString("autopro_visible");
                                adminInfo.singup_steps = mJsonObject.getString("singup_steps");

                                SplashActivity.checkChatLogin();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Network Error", Toast.LENGTH_LONG).show();
                        //Log.d("myStatus",error.toString());

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("phoneverify","true");
                params.put("phone", adminPhone);
                params.put("password", adminPass);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(context).addToRequest(stringRequest);
    }


    public static boolean checkInternet() {
        try {
            //Check for internet
            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnected()) {
                return true;
                //Toast.makeText(getApplicationContext(), "connected", Toast.LENGTH_SHORT).show();
            } else {
                return false;
                //Toast.makeText(getApplicationContext(), "disconnected", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){

        }
        return true;
    }

    public static List<UserInfo> userDetails, adminDetails, activeDoctorDetails = new ArrayList<>();
    public static List<String> locCheck = new ArrayList<>();
    public static String adminPhone, adminPass, doctorPhone, doctorPass;
    public static List<LatLng> userLatLng = new ArrayList<>();
    public static int currentMarker = -1;


    public static class UserInfo {
        public String phone;
        public String password;
        public String pic_path;
        public String fname;
        public String lname;
        public String blood_group;
        public String birth_date;
        public String age;
        public String last_donation;
        public String new_donor;
        public String email;
        public String division;
        public String district;
        public String upazila;

        public String address;
        public Double latitude;
        public Double longitude;
        public String code;
        public String verification;
        public Double lastLat;
        public Double lastLng;
        public String fcm_email;
        public String fcm_uid;
        public String fcm_token;

        public String pro_visible;
        public String called_date;
        public String called_today;
        public String donations_number;
        public String user_type;

        public String gender;
        public String already_donated;
        public String autopro_visible;
        public String singup_steps;
        public String post_code;
        public String rank;
        public String web_url;
        public String fb_url;
        public String religion;
        public String is_physically_disble;
        public String nationality;
        public String nid;
        public String status;
        //public LatLng latLng;
    }

    public static List<DoctorInfo> doctorDetails = new ArrayList<>();

    public static class DoctorInfo {
        public String id;
        public String name;
        public String designation;
        public String hospital;
        public String speacilist;
        public String division;
        public String district;
        public String upazila;
        public String phone;
        public String email;
        public String gender;
        public String profile_photo;
        public String preasent_address;
        public String doctor_detail;
        public String chamber_address;

    }


    public static List<HospitalInfo> hospitalDetails = new ArrayList<>();

    public static class HospitalInfo {
        public String pic_path;
        public String name;
        public String address;
        public String speciality;
        public String phone;
        public String latitude;
        public String longitude;
        public String division;
        public String district;
        public String thana;
    }



    public static List<AdminChatListInfo> chatListDetails = new ArrayList<>();
    public static String chatProfilePhone;
    public static UserInfo donorChatProfile;

    public static class AdminChatListInfo {
        public String id;
        public String donor_phone;
        public String donor_fcm_uid;
        public String admin_phone;
        public String pic_path;
        public String name;
        public String address;
        public String lastmsgtime;
    }

    public static UserInfo getdonorChatProfile() {
        if (donorChatProfile == null) {
            setdonorChatProfile();
        }
        return donorChatProfile;
    }


    private static void setdonorChatProfile() {
        //Log.d("AdminInfo",context.getString(R.string.admin_phone)+" "+context.getString(R.string.admin_pass));
        String app_server_url = context.getString(R.string.server_url) + "getdonor_chatprofile.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, app_server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject mJsonObject;
                            donorChatProfile = new UserInfo();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                mJsonObject = jsonArray.getJSONObject(i);

                                //insert userinfo into userDetails list arrays

                                //pic_path,fname,lname,email,phone,birthDate,bloodgroup,location,latitude,longitude
                                donorChatProfile.phone = mJsonObject.getString("phone");
                                donorChatProfile.password = mJsonObject.getString("password");
                                donorChatProfile.pic_path = mJsonObject.getString("pic_path");
                                donorChatProfile.fname = mJsonObject.getString("fname");
                                donorChatProfile.lname = mJsonObject.getString("lname");
                                donorChatProfile.blood_group = mJsonObject.getString("blood_group");
                                donorChatProfile.birth_date = mJsonObject.getString("birth_date");
                                donorChatProfile.age = mJsonObject.getString("age");
                                donorChatProfile.last_donation = mJsonObject.getString("last_donation");
                                donorChatProfile.new_donor = mJsonObject.getString("new_donor");
                                donorChatProfile.email = mJsonObject.getString("email");
                                donorChatProfile.division = mJsonObject.getString("division");
                                donorChatProfile.district = mJsonObject.getString("district");
                                donorChatProfile.upazila = mJsonObject.getString("upazila");

                                donorChatProfile.address = mJsonObject.getString("address");

                                if (mJsonObject.getString("latitude").equals("null") ||
                                        mJsonObject.getString("latitude").equals("na") ||
                                        mJsonObject.getString("longitude").equals("null") ||
                                        mJsonObject.getString("longitude").equals("na")) {

                                    donorChatProfile.latitude = 0.0;
                                    donorChatProfile.longitude = 0.0;

                                } else {
                                    try {
                                        donorChatProfile.latitude = Double.parseDouble(mJsonObject.getString("latitude"));
                                        donorChatProfile.longitude = Double.parseDouble(mJsonObject.getString("longitude"));
                                    } catch (Exception e) {

                                    }
                                }
                                donorChatProfile.code = mJsonObject.getString("code");
                                donorChatProfile.verification = mJsonObject.getString("verification");

                                if (mJsonObject.getString("lastLat").equals("null") ||
                                        mJsonObject.getString("lastLat").equals("na") ||
                                        mJsonObject.getString("lastLng").equals("null") ||
                                        mJsonObject.getString("lastLng").equals("na")) {

                                    donorChatProfile.lastLat = 0.0;
                                    donorChatProfile.lastLng = 0.0;

                                } else {
                                    try {
                                        donorChatProfile.lastLat = Double.parseDouble(mJsonObject.getString("lastLat"));
                                        donorChatProfile.lastLng = Double.parseDouble(mJsonObject.getString("lastLng"));
                                    } catch (Exception e) {

                                    }
                                }


                                donorChatProfile.fcm_email = mJsonObject.getString("fcm_email");
                                donorChatProfile.fcm_uid = mJsonObject.getString("fcm_uid");
                                donorChatProfile.fcm_token = mJsonObject.getString("fcm_token");


                                donorChatProfile.pro_visible = mJsonObject.getString("pro_visible");
                                donorChatProfile.called_date = mJsonObject.getString("called_date");
                                donorChatProfile.called_today = mJsonObject.getString("called_today");
                                donorChatProfile.donations_number = mJsonObject.getString("donations_number");
                                donorChatProfile.user_type = mJsonObject.getString("user_type");
                                donorChatProfile.gender = mJsonObject.getString("gender");
                                donorChatProfile.already_donated = mJsonObject.getString("already_donated");
                                donorChatProfile.autopro_visible = mJsonObject.getString("autopro_visible");
                                donorChatProfile.singup_steps = mJsonObject.getString("singup_steps");

                                String user = new SharedPrefUtil(context).getString(DataLoader.USERTYPE, "na");
                                String loginStatus = new SharedPrefUtil(context).getString(DataLoader.LIVECHATLOGIN, "false");

                                if (user.equals("admin") && !user.equals("na") && !DataLoader.doctorCarePanel) {
                                    try {
                                        SplashActivity.startAdminChat();
                                    }catch(Exception e){

                                    }

                                } else if (user.equals("doctor") && !user.equals("na") && DataLoader.doctorCarePanel
                                        && DataLoader.doctorToDonorChat) {

                                    SplashActivity.startDoctorToDonorChat();

                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            //Log.d("chatListDetails", e.toString());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Network Error", Toast.LENGTH_LONG).show();
                        //Log.d("myStatus",error.toString());

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("phoneverify","true");
                params.put("phone", chatProfilePhone);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(context).addToRequest(stringRequest);
    }

    public static void insertDoctorChatList() {
        Log.d("DoctorPanel", profileInfo.phone + "And " + doctorPhone);
        //Toast.makeText(context, "delete "+id, Toast.LENGTH_LONG).show();
        //Log.d("insertAdinChatList","request_type"+ "insert"+"donor_phone"+ profileInfo.phone+"admin_phone"+ DataLoader.adminPhone
        //+"pic_path"+ profileInfo.pic_path+"name"+ profileInfo.fname+" "+profileInfo.lname+"address"+ profileInfo.address);
        String app_server_url = context.getString(R.string.server_url) + "delete_patientchat.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, app_server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(context, response, Toast.LENGTH_LONG).show();
                        if (response.equals("inserted")) {

                            Log.d("DoctorPanel", "Inserted for " + profileInfo.phone + "And " + doctorPhone);
                            //Toast.makeText(context,"Donor inserted into admin chat list.",Toast.LENGTH_LONG).show();
                        } else {
                            //Toast.makeText(context,"Couldn't be inserted",Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("request_type", "insert");
                params.put("donor_phone", profileInfo.phone);
                params.put("doctor_phone", DataLoader.doctorPhone);
                params.put("pic_path", profileInfo.pic_path);
                params.put("name", profileInfo.fname + " " + profileInfo.lname);
                params.put("address", profileInfo.address);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(context).addToRequest(stringRequest);


    }

    public static void insertAdminChatList() {
        //Toast.makeText(context, "delete "+id, Toast.LENGTH_LONG).show();
        //Log.d("insertAdinChatList","request_type"+ "insert"+"donor_phone"+ profileInfo.phone+"admin_phone"+ DataLoader.adminPhone
        //+"pic_path"+ profileInfo.pic_path+"name"+ profileInfo.fname+" "+profileInfo.lname+"address"+ profileInfo.address);
        String app_server_url = context.getString(R.string.server_url) + "delete_donorchat.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, app_server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(context, response, Toast.LENGTH_LONG).show();
                        if (response.equals("inserted")) {

                            //Toast.makeText(context,"Donor inserted into admin chat list.",Toast.LENGTH_LONG).show();
                        } else {
                            //Toast.makeText(context,"Couldn't be inserted",Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("request_type", "insert");
                params.put("donor_phone", profileInfo.phone);
                params.put("admin_phone", DataLoader.adminPhone);
                params.put("pic_path", profileInfo.pic_path);
                params.put("name", profileInfo.fname + " " + profileInfo.lname);
                params.put("address", profileInfo.address);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(context).addToRequest(stringRequest);


    }

    public static void updateDoctorChatList(final String donor_phone, final String admin_phone) {
        //Toast.makeText(context, "delete "+id, Toast.LENGTH_LONG).show();
        //Log.d("insertAdinChatList","request_type"+ "update"+"donor_phone"+ donor_phone+"admin_phone"+ admin_phone);
        String app_server_url = context.getString(R.string.server_url) + "delete_patientchat.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, app_server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(context, response, Toast.LENGTH_LONG).show();
                        if (response.equals("updated")) {

                            //Toast.makeText(context,"Donor updated into admin chat list.",Toast.LENGTH_LONG).show();
                        } else {
                            //Toast.makeText(context,"Couldn't be updated",Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("request_type", "update");
                params.put("donor_phone", donor_phone);
                params.put("admin_phone", admin_phone);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(context).addToRequest(stringRequest);


    }

    public static void updateAdminChatList(final String donor_phone, final String admin_phone) {
        //Toast.makeText(context, "delete "+id, Toast.LENGTH_LONG).show();
        //Log.d("insertAdinChatList","request_type"+ "update"+"donor_phone"+ donor_phone+"admin_phone"+ admin_phone);
        String app_server_url = context.getString(R.string.server_url) + "delete_donorchat.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, app_server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(context, response, Toast.LENGTH_LONG).show();
                        if (response.equals("updated")) {

                            //Toast.makeText(context,"Donor updated into admin chat list.",Toast.LENGTH_LONG).show();
                        } else {
                            //Toast.makeText(context,"Couldn't be updated",Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("request_type", "update");
                params.put("donor_phone", donor_phone);
                params.put("admin_phone", admin_phone);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(context).addToRequest(stringRequest);


    }


    public static String formatDate(String date) {
        try {
            String[] bDate = date.split("-");
            date = bDate[2] + "-" + bDate[1] + "-" + bDate[0];
        } catch (Exception e) {

        }
        return date;
    }

    public static String reverseDate(String date) {
        String[] bDate = date.split("-");
        date = bDate[0] + "-" + bDate[1] + "-" + bDate[2];
        return date;
    }


    public static String imageEncoded, pic_name, new_pic_name;
    public static FragmentManager manager;

    public static void uploadProfilePhoto() {
        //Log.d("PhotoTrace","imageEncoded: "+imageEncoded);
        //Log.d("PhotoTrace","phone: "+getUserPhone()+" pic_name: "+pic_name+" new_pic_name: "+new_pic_name);
        String app_server_url = context.getString(R.string.server_url) + "upload_profilephoto.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, app_server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("uploaded")) {

                            Toast.makeText(context, "Profile Picture was uploaded successfully", Toast.LENGTH_LONG).show();
                            //finish();
                            //}
                        }
                        if (response.equals("updated")) {

                            Toast.makeText(context, "Profile Picture was updated successfully", Toast.LENGTH_LONG).show();
                            //finish();
                            //}
                        } else {
                            //Toast.makeText(context, response, Toast.LENGTH_LONG).show();
//                            ErrorMsg.msg = "Your profile Picture was not saved. It may cause due to slow" +
//                                    "internet connection. You can update it later from Profile Section.";
//                            ErrorMsg photoerror = new ErrorMsg();
//                            photoerror.show(manager,"photoerror");
                        }
                        //AllStaticVar.writeLocal(getActivity());
                        //Log.d("singup", AllStaticVar.readLocal(getActivity()));

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(context, "Your profile Picture was not saved.", Toast.LENGTH_LONG).show();
//                        ErrorMsg.msg = "Your profile Picture was not saved. It may cause due to slow" +
//                                "internet connection. You can update it later from Profile Section.";
//                        ErrorMsg photoerror = new ErrorMsg();
//                        photoerror.show(manager,"photoerror");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("phone", getUserPhone());
                params.put("imageEncoded", imageEncoded);
                params.put("pic_name", pic_name);
                params.put("new_pic_name", new_pic_name);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(context).addToRequest(stringRequest);
    }

    static NotificationCompat.Builder mBuilder;
    static NotificationManager mNotifyMgr;
    static int mNotificationId;


    public static List<BloodRequests> bloodRequests = new ArrayList<>();

    public static class BloodRequests {
        public String request_phone;
        public String request_blood;
        public String request_address;
        public String request_to;
        public String request_time;
        public String accepted;
        public String deletion_date;
    }


    public static void updateFcmInfo(final String requestFrom) {
//        Log.d("updateMySqlDB","phone: "+ DataLoader.getUserPhone()
//                +" fcm_email: "+RegisterFragment.fcm_email
//                +" fcm_uid: "+RegisterFragment.fcm_uid
//        );
        String app_server_url = context.getString(R.string.server_url) + "update_fcminfo.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, app_server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(context, response, Toast.LENGTH_LONG).show();

                        if (response.equals("updated")) {
                            if (requestFrom.equals("Login")) {
                                //Login.FcmSuccess();
                                TalkToProfileDetails talk;
                                talk = (Login) context;
                                talk.setUserDetails();
                            }
                            if (requestFrom.equals("BasicInformation")) {
                                //BasicInformation.FcmSuccess();
                                TalkToProfileDetails talk;
                                talk = (BasicInformation) context;
                                talk.setUserDetails();
                            }
                            //startActivity(new Intent(getActivity(), MainActivity.class));

                        } else {

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("phoneverify","true");
                params.put("fcm_email", "" + RegisterFragment.fcm_email);
                params.put("fcm_uid", "" + RegisterFragment.fcm_uid);
                params.put("phone", DataLoader.getUserPhone());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(context).addToRequest(stringRequest);
    }


    public static void removeLocalVars() {


        new SharedPrefUtil(context).saveString(DataLoader.PHONE_NUMBER, null);
        new SharedPrefUtil(context).saveString(DataLoader.PASSWORD, null);
        new SharedPrefUtil(context).saveString(DataLoader.USERTYPE, null);
        new SharedPrefUtil(context).saveString(DataLoader.BLOODGROUP, null);
        new SharedPrefUtil(context).saveString(DataLoader.LOGIN, "false");
        new SharedPrefUtil(context).saveInt(DataLoader.NOTIFICATIONID, 0);
        new SharedPrefUtil(context).saveInt(DataLoader.RECENTCHAT, 0);
        new SharedPrefUtil(context).saveString(DataLoader.PROCHECKDATE, null);
        new SharedPrefUtil(context).saveString(DataLoader.LOGOUT, "true");
        new SharedPrefUtil(context).saveString(DataLoader.HOTLINE, null);
        new SharedPrefUtil(context).saveString(DataLoader.AMBULANCE, null);
        new SharedPrefUtil(context).saveString(DataLoader.LIVECHATLOGIN, "false");
        DataLoader.livechatlogin = "false";
        DataLoader.userPhone = null;
        DataLoader.userPass = null;
        DataLoader.userType = null;
        DataLoader.blood_group = null;
        DataLoader.hotline = null;
        DataLoader.ambulane = null;
        DataLoader.tokenChanged = false;

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseAuth.getInstance().signOut();
            //mOnLogoutListener.onSuccess("Successfully logged out!");
        } else {
            //mOnLogoutListener.onFailure("No user logged in yet!");
        }


    }

    public static void changedTokenLogout(final Context context) {
        /*new AllDialog(context).showDialog("Please Login Again!", "LifeCycle token key has been changed. Please log in again to continue.",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DataLoader.context = context;
                        DataLoader.removeLocalVars();

                        Intent intent = new Intent(context, Login.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                }, "OK", "NO");*/
        try {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(context);
            builder.setTitle("Please Login Again!");
            builder.setMessage("LifeCycle token key has been changed. Please log in again to continue.");


            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DataLoader.context = context;
                    DataLoader.removeLocalVars();

                    Intent intent = new Intent(context, Login.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });


            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });

            builder.setCancelable(false);

            builder.show();
        }catch (Exception e){

        }
    }

    public static void checkLogin(Context context) {

        try {
            String login = new SharedPrefUtil(context).getString(DataLoader.LOGIN, "false");
            String logout = new SharedPrefUtil(context).getString(DataLoader.LOGOUT, "false");

            if (logout.equals("true") && login.equals("false")) {
                changedTokenLogout(context);
            }
        }catch (Exception e){

        }

    }

    public static void sendSms(final String mobileNumber, final String code, final String type) {
        String sms_url = context.getString(R.string.server_url) + "onnosms.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, sms_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(context, response, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Log.d("sms",code+" "+mobileNumber);
                Map<String, String> params = new HashMap<String, String>();
                params.put("code", "" + code);
                params.put("phone", mobileNumber);
                params.put("type", type);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(context).addToRequest(stringRequest);
    }

    public static void hideKeyboard(View v, Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(v.getContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
    }

    public static void updateUserStatus(FirebaseAuth mAuth, DatabaseReference rootRef, String state)
    {
        String saveCurrentTime, saveCurrentDate;

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));  // IMP !!!
        String datetime = sdf.format(calendar.getTime());

        HashMap<String, Object> onlineStateMap = new HashMap<>();
        onlineStateMap.put("time", saveCurrentTime);
        onlineStateMap.put("date", datetime);
        onlineStateMap.put("state", state);

        String currentUserId = mAuth.getCurrentUser().getUid();

        rootRef.updateChildren(onlineStateMap);
    }

    public static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(androidx.fragment.app.FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        return null;
    }
}
