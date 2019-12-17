package com.ourdreamit.blooddonationproject.fcm;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import com.ourdreamit.blooddonationproject.fcm.CurrentLocation;

/**
 * Created by anismizi on 5/10/17.
 */

public class GpsLocationReceiver extends BroadcastReceiver {
    String locationMode;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
            //Toast.makeText(context, "in android.location.PROVIDERS_CHANGED",
                    //Toast.LENGTH_SHORT).show();

            ContentResolver contentResolver = context.getContentResolver();
            // Find out what the settings say about which providers are enabled
            Intent myIntent = new Intent(context, CurrentLocation.class);
            int mode = Settings.Secure.getInt(
                    contentResolver, Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF);

            if (mode != Settings.Secure.LOCATION_MODE_OFF) {
                //Toast.makeText(context, "Location is On", Toast.LENGTH_SHORT).show();
                context.startService(myIntent);
                if (mode == Settings.Secure.LOCATION_MODE_HIGH_ACCURACY) {
                    locationMode = "High accuracy. Uses GPS, Wi-Fi, and mobile networks to determine location";
                } else if (mode == Settings.Secure.LOCATION_MODE_SENSORS_ONLY) {
                    locationMode = "Device only. Uses GPS to determine location";
                } else if (mode == Settings.Secure.LOCATION_MODE_BATTERY_SAVING) {
                    locationMode = "Battery saving. Uses Wi-Fi and mobile networks to determine location";
                }
            }else {
                context.stopService(myIntent);
                //Toast.makeText(context, "Location is Off",
                        //Toast.LENGTH_SHORT).show();
            }
            //Intent pushIntent = new Intent(context, LocalService.class);
            //context.startService(pushIntent);


        }
    }
}
