package com.ourdreamit.blooddonationproject.utils;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class NetworkSchedulerService extends JobService implements
        ConnectivityReceiver.ConnectivityReceiverListener {

    //private static final String TAG = NetworkSchedulerService.class.getSimpleName();

    private ConnectivityReceiver mConnectivityReceiver;

    FirebaseAuth mAuth;
    String currentUserId;
    DatabaseReference rootRef;

    @Override
    public void onCreate() {
        super.onCreate();
        //Log.i(TAG, "Service created");

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null) {
            currentUserId = mAuth.getCurrentUser().getUid();
            rootRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId).child("userState");
        }

        mConnectivityReceiver = new ConnectivityReceiver(this);
    }


    /**
     * When the app's NetworkConnectionActivity is created, it starts this service. This is so that the
     * activity and this service can communicate back and forth. See "setUiCallback()"
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log.i(TAG, "onStartCommand");
        return START_NOT_STICKY;
    }


    @Override
    public boolean onStartJob(JobParameters params) {
        //Log.i(TAG, "onStartJob" + mConnectivityReceiver);
        registerReceiver(mConnectivityReceiver, new IntentFilter(Constants.CONNECTIVITY_ACTION));
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        //Log.i(TAG, "onStopJob");
        unregisterReceiver(mConnectivityReceiver);
        return true;
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        String message = isConnected ? "online" : "offline";
        //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

        if(currentUserId != null) {
            DataLoader.updateUserStatus(mAuth, rootRef, message);

            rootRef.onDisconnect().setValue("offline");
        }
    }
}
