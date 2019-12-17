package com.ourdreamit.blooddonationproject.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.ourdreamit.blooddonationproject.ui.adapters.ActiveDoctorListAdapter;
import com.ourdreamit.blooddonationproject.utils.DataLoader;
import com.ourdreamit.blooddonationproject.ui.adapters.DoctorChatListAdapter;
import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.utils.SharedPrefUtil;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_TIME_MS = 2000;
    private Handler mHandler;
    private Runnable mRunnable;
    public static Context context;
    String userType,livechatlogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        context = this;

        try {
            // check if user is already logged in or not
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                //Log.d("Finaltrace","SplashActivity oncreate");

                userType = new SharedPrefUtil(context).getString(DataLoader.USERTYPE, "na");
                livechatlogin = new SharedPrefUtil(context).getString(DataLoader.LIVECHATLOGIN, "false");

                if (userType.equals("donor") && !userType.equals("na")
                        && livechatlogin.equals("true") && !DataLoader.doctorCarePanel) {

                    DataLoader.context = getApplicationContext();

                    if (DataLoader.tokenChanged) {
                        DataLoader.changedTokenLogout(this);
                    }

                    DataLoader.adminInfo = null;
                    DataLoader.getAdminInfo();

                } else if (userType.equals("admin") && !userType.equals("na")
                        && livechatlogin.equals("true") && !DataLoader.doctorCarePanel) {

                    DataLoader.context = getApplicationContext();

                    if (DataLoader.tokenChanged) {
                        DataLoader.changedTokenLogout(this);
                    }

                    DataLoader.donorChatProfile = null;
                    DataLoader.getdonorChatProfile();

                } else if (userType.equals("donor") && !userType.equals("na")
                        && livechatlogin.equals("true") && DataLoader.doctorCarePanel
                        && DataLoader.donorToDoctorChat) {

                    if (DataLoader.tokenChanged) {
                        DataLoader.changedTokenLogout(this);
                    }

                    startDonorToDoctorChat();

                } else if (userType.equals("doctor") && !userType.equals("na")
                        && livechatlogin.equals("true") && DataLoader.doctorCarePanel
                        && DataLoader.doctorToDonorChat) {

                    DataLoader.context = getApplicationContext();

                    if (DataLoader.tokenChanged) {
                        DataLoader.changedTokenLogout(this);
                    }

                    DataLoader.donorChatProfile = null;
                    DataLoader.getdonorChatProfile();

                } else {
                    LoginActivity.startIntent(SplashActivity.this);
                }

            } else {

                LoginActivity.startIntent(SplashActivity.this);
            }
            //finish();
//            }
//        };
        }catch (Exception e){

        }
//
//        mHandler.postDelayed(mRunnable, SPLASH_TIME_MS);
    }

    @Override
    public void onBackPressed() {
        try {
            DataLoader.tokenChanged = false;
            finish();
        }catch (Exception e){

        }
    }

    public static void checkChatLogin(){

            ChatActivity.startActivity(context,
                    DataLoader.adminInfo.fcm_email,
                    DataLoader.adminInfo.fcm_uid,
                    DataLoader.adminInfo.fcm_token
            );
        //context.startActivity(new Intent(context, AdminList.class));
    }

    public static void startAdminChat(){

            //AdminChatListAdapter.dialog.dismiss();
            DataLoader.isAdminChatting = true;
            ChatActivity.startActivity(context,
                    DataLoader.donorChatProfile.fcm_email,
                    DataLoader.donorChatProfile.fcm_uid,
                    DataLoader.donorChatProfile.fcm_token
            );

    }

    private void startDonorToDoctorChat(){

        if(ActiveDoctorListAdapter.loadDonorToDoctor){
            ActiveDoctorListAdapter.progressDialog.dismiss();
            ActiveDoctorListAdapter.loadDonorToDoctor = false;
        }

        ChatActivity.startActivity(context,
                DataLoader.activeDoctorDetails.get(DataLoader.currentMarker).fcm_email,
                DataLoader.activeDoctorDetails.get(DataLoader.currentMarker).fcm_uid,
                DataLoader.activeDoctorDetails.get(DataLoader.currentMarker).fcm_token
        );
    }

    public static void startDoctorToDonorChat(){

        if(DoctorChatListAdapter.loadDoctorToDonor){
            DoctorChatListAdapter.progressDialog.dismiss();
            DoctorChatListAdapter.loadDoctorToDonor = false;
        }

//        Log.d("Finaltrace",DataLoader.donorChatProfile.fcm_email+" "+DataLoader.donorChatProfile.fcm_uid+" "+
//                DataLoader.donorChatProfile.fcm_token);

        DataLoader.isDoctorChatting = true;
        ChatActivity.startActivity(context,
                DataLoader.donorChatProfile.fcm_email,
                DataLoader.donorChatProfile.fcm_uid,
                DataLoader.donorChatProfile.fcm_token
        );
    }

    /*@Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.postDelayed(mRunnable, SPLASH_TIME_MS);
    }*/
}
