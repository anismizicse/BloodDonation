package com.ourdreamit.blooddonationproject.fcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

public class NetworkReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm =(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo()!=null){
            //Toast.makeText(context, "Connected to Internet", Toast.LENGTH_LONG).show();
            Intent myIntent = new Intent(context, ProVisibilityService.class);
            context.startService(myIntent);
        }
        else {
            //Toast.makeText(context, "Disconnected to Internet", Toast.LENGTH_LONG).show();
        }
    }


}