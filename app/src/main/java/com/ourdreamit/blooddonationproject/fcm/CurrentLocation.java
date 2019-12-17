package com.ourdreamit.blooddonationproject.fcm;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.ourdreamit.blooddonationproject.utils.DataLoader;
import com.ourdreamit.blooddonationproject.utils.MySingleton;
import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.utils.SharedPrefUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by anismizi on 5/11/17.
 */

public class CurrentLocation extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    int stop = 0;
    String address;
    double latitude;
    double longitude;
    

    @Override
    public void onCreate() {

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent,  int flags, int startId) {

        //Toast.makeText(this, "Service running", Toast.LENGTH_SHORT).show();
        stop = 0;
        //DataLoader.context = this;
        //AllStaticVar.updateDatabase();

        final Handler handler = new Handler(){

            @Override
            public void handleMessage(Message msg) {
                mGoogleApiClient.connect();
                // TODO Auto-generated method stub
                super.handleMessage(msg);
                //Toast.makeText(getApplicationContext(), "10 secs has passed", Toast.LENGTH_SHORT).show();
//                if(displayLocationSettingsRequest(getApplicationContext()) && checkInternet()){
//                    Toast.makeText(getApplicationContext(), "All Satisfied", Toast.LENGTH_SHORT).show();
//                }else {
//                    Toast.makeText(getApplicationContext(), "Something Missing", Toast.LENGTH_SHORT).show();
//                }
                //Check for internet
                ConnectivityManager cm =
                        (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if(activeNetwork != null && activeNetwork.isConnected()) {
                    displayLocationSettingsRequest(getApplicationContext());
                    //Toast.makeText(getApplicationContext(), "connected", Toast.LENGTH_SHORT).show();
                }else {
                    //Toast.makeText(getApplicationContext(), "disconnected", Toast.LENGTH_SHORT).show();
                }


            }

        };



        new Thread(new Runnable(){
            public void run() {
                // TODO Auto-generated method stub
                while(true)
                {
                    if(stop == 1){
                        break;
                    }
                    try {
                        Thread.sleep(1800000);
                        //Thread.sleep(10000);
                        handler.sendEmptyMessage(0);

                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }

            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }

    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());


        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        //Log.i("loctrace", "All location settings are satisfied.");
                        //Toast.makeText(getApplicationContext(), "gps enabled", Toast.LENGTH_LONG).show();
                        updateDatabase();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        //Log.i("loctrace", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");
                        //Toast.makeText(getApplicationContext(), "disabled", Toast.LENGTH_LONG).show();
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            //status.startResolutionForResult(MainActivity.class, REQUEST_CHECK_SETTINGS);

                        } catch (Exception e) {
                            //Log.i("loctrace", "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        //Log.i("loctrace", "Location settings are inadequate, and cannot be fixed here. Dialog not created.");

                        break;
                }
            }
        });

    }

    public  void updateDatabase() {
        String app_server_url = getString(R.string.server_url) + "update_location.php";
        //Toast.makeText(AllStatic.ctx, "inside updateDatabase", Toast.LENGTH_LONG).show();
        //Log.d("updateLat","phone "+getUserPhone()+" latitude "+ " "+latitude+"longitude"+ " "+longitude+" address "+address);
        try {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, app_server_url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //Toast.makeText(CurrentLocation.this, response, Toast.LENGTH_LONG).show();

                            if (!response.equals("na")) {
                                try {
                                    JSONArray jsonArray = new JSONArray(response);
                                    JSONObject mJsonObject;

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        mJsonObject = jsonArray.getJSONObject(i);
                                        String password = mJsonObject.getString("password");
                                        String fcm_email = mJsonObject.getString("fcm_email");
                                        String fcm_uid = mJsonObject.getString("fcm_uid");
                                        String email = mJsonObject.getString("email");
                                        String fcm_token = mJsonObject.getString("fcm_token");

                                        String local_Token = new SharedPrefUtil(CurrentLocation.this).getString(DataLoader.FCM_TOKEN, null);
                                        //Toast.makeText(CurrentLocation.this,local_Token+"\n "+fcm_token,Toast.LENGTH_LONG).show();
                                        if(local_Token != null && !fcm_token.equals(local_Token)){

                                            DataLoader.context = CurrentLocation.this;
                                            DataLoader.removeLocalVars();
                                        }

                                    }

                                } catch (JSONException e) {
                                    //e.printStackTrace();
                                }

                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //Log.d("updateLat",error.toString());
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("phone", new SharedPrefUtil(CurrentLocation.this).getString(DataLoader.PHONE_NUMBER, "na"));
                    params.put("latitude", ""+latitude);
                    params.put("longitude", ""+longitude);
                    params.put("address", address);

                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MySingleton.getmInstance(this).addToRequest(stringRequest);
        }catch (Exception e){
            //Toast.makeText(this, ""+e, Toast.LENGTH_LONG).show();
        }

        //checkProVisibility();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        mGoogleApiClient.disconnect();
        stop = 1;
        //Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
            Geocoder gc = new Geocoder(this, Locale.getDefault());

            try {
                List<Address> list = gc.getFromLocation(latitude,longitude,1);
                address = list.get(0).getAddressLine(0);
                //locality = add.getLocality();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
