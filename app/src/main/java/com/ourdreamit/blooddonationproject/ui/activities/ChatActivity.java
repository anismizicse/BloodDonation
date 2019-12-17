package com.ourdreamit.blooddonationproject.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ourdreamit.blooddonationproject.utils.DataLoader;
import com.ourdreamit.blooddonationproject.utils.FirebaseChatMainApp;
import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.utils.SetToolbar;
import com.ourdreamit.blooddonationproject.ui.fragments.ChatFragment;
import com.ourdreamit.blooddonationproject.utils.Constants;
import com.ourdreamit.blooddonationproject.utils.SharedPrefUtil;

public class ChatActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private ImageView user_online_status;
    private String userType;
    DatabaseReference stateRef;
    ValueEventListener stateListener;
    //private static final String TAG = "ChatActivity";

    public static void startActivity(Context context,
                                     String receiver,
                                     String receiverUid,
                                     String firebaseToken) {

        //Log.d("Finaltrace",context+" ChatActivity startActivity");
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(Constants.ARG_RECEIVER, receiver);
        intent.putExtra(Constants.ARG_RECEIVER_UID, receiverUid);
        intent.putExtra(Constants.ARG_FIREBASE_TOKEN, firebaseToken);
        context.startActivity(intent);

    }

    @Override
    public void onBackPressed() {
        try {
            String user_type = DataLoader.getUserType();
            if (user_type.equals("admin") && !DataLoader.doctorCarePanel) {
                DataLoader.isAdminChatting = false;
                startActivity(new Intent(this, AdminChatList.class));
                finish();
            } else if (user_type.equals("donor") && !DataLoader.doctorCarePanel) {
                new SharedPrefUtil(this).saveInt(DataLoader.RECENTCHAT, 0);
                startActivity(new Intent(this, AdminList.class));
                finish();
            } else if (user_type.equals("doctor") && DataLoader.doctorCarePanel && DataLoader.doctorToDonorChat) {
                //Log.d("Finaltrace","DoctorCare started");
                DataLoader.isDoctorChatting = false;
                startActivity(new Intent(this, DoctorCare.class));
                finish();
            } else if (user_type.equals("donor") && DataLoader.doctorCarePanel && DataLoader.donorToDoctorChat) {
                new SharedPrefUtil(this).saveInt(DataLoader.RECENTCHAT, 0);
                startActivity(new Intent(this, DoctorCare.class));
                finish();
            } else {
                new SharedPrefUtil(this).saveInt(DataLoader.RECENTCHAT, 0);
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }

            DataLoader.tokenChanged = false;
        }catch (Exception e){

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.d("Finaltrace","ChatActivity onCreate");
        setContentView(R.layout.activity_chat);
        bindViews();
        init();

        DataLoader.context = this;
        DataLoader.checkLogin(this);

        if(DataLoader.tokenChanged){
            DataLoader.changedTokenLogout(this);
        }
    }

    private void bindViews() {
        //Log.d("ChatTrace","ChatActivity bindViews");
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        user_online_status = findViewById(R.id.user_online_status);
    }

    private void init() {
        //Log.d("ChatTrace","ChatActivity init");
        // set the toolbar
        //setSupportActionBar(mToolbar);

        userType = new SharedPrefUtil(this).getString(DataLoader.USERTYPE, "na");

        // set toolbar title
        if(userType.equals("donor") && !userType.equals("na") && !DataLoader.doctorCarePanel) {
            mToolbar.setTitle("Admin");
        }else if(userType.equals("donor") && !userType.equals("na") && DataLoader.doctorCarePanel && DataLoader.donorToDoctorChat) {
            mToolbar.setTitle("Doctor");
        }else{
            mToolbar.setTitle(getIntent().getExtras().getString(Constants.ARG_RECEIVER));

            String receiver_id = getIntent().getExtras().getString(Constants.ARG_RECEIVER_UID);
            //Log.d(TAG, "init: "+receiver_id);

            stateRef = FirebaseDatabase.getInstance().getReference().child("users").child(receiver_id).child("userState");

            stateListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    if (dataSnapshot.hasChild("state")) {
                        String state = dataSnapshot.child("state").getValue().toString();

                        if (state.equals("online")) {

                            user_online_status.setVisibility(View.VISIBLE);

                        } else if (state.equals("offline")) {

                            user_online_status.setVisibility(View.GONE);
                        }
                    } else {
                        user_online_status.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            stateRef.addValueEventListener(stateListener);
        }
        mToolbar.setBackgroundColor(SetToolbar.setBgColor());

//        Log.d("Finaltrace","ChatFragment onCreateView Called"+ DataLoader.doctorCarePanel+" "+
//                DataLoader.donorToDoctorChat+" "+
//                DataLoader.doctorToDonorChat);

        // set the register screen fragment
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_content_chat,
                ChatFragment.newInstance(getIntent().getExtras().getString(Constants.ARG_RECEIVER),
                        getIntent().getExtras().getString(Constants.ARG_RECEIVER_UID),
                        getIntent().getExtras().getString(Constants.ARG_FIREBASE_TOKEN)),
                ChatFragment.class.getSimpleName());
        fragmentTransaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Log.d("ChatTrace","ChatActivity onResume");
        FirebaseChatMainApp.setChatActivityOpen(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Log.d("ChatTrace","ChatActivity onPause");
        FirebaseChatMainApp.setChatActivityOpen(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(userType.equals("admin") && stateRef != null){
            stateRef.removeEventListener(stateListener);
        }
    }
}
