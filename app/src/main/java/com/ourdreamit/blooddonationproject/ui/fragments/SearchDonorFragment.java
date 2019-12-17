package com.ourdreamit.blooddonationproject.ui.fragments;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.utils.SetToolbar;
import com.ourdreamit.blooddonationproject.dialogs.AllDialog;
import com.ourdreamit.blooddonationproject.fcm.FcmNotificationBuilder;
import com.ourdreamit.blooddonationproject.ui.activities.Login;
import com.ourdreamit.blooddonationproject.utils.DataLoader;
import com.ourdreamit.blooddonationproject.utils.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class SearchDonorFragment extends AppCompatActivity implements OnMapReadyCallback,
        LocationListener,
         GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener,GoogleMap.OnInfoWindowLongClickListener,
        GoogleMap.OnInfoWindowCloseListener,View.OnClickListener {


    private static final String TAG = "GPSTAG";
    public static final int REQUEST_CHECK_SETTINGS = 1;
    private GoogleMap mMap;
    double latitude;
    double longitude;

    //GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    View view;
    public static Location updateLocation;
    public static int reloaded;
    private FusedLocationProviderClient mFusedLocationClient;



    public ProgressDialog dialog;
    private Toolbar toolbar;

    private AppCompatButton blood_request;


    static int totalDonorsSent = 0;

    static final float COORDINATE_OFFSET = 0.00002f; // You can change this value according to your need
    HashMap<String, String> markerLocation;    // HashMap of marker identifier and its location as a string
    private static  int MAX_NUMBER_OF_MARKERS = 0;
    public static Context context;
    public static String request_to;
    static android.app.FragmentManager manager;
    @Override
    public void onInfoWindowClick(Marker marker) {
        //Toast.makeText(getApplicationContext(),"onInfoWindowClick "+DataLoader.userLatLng.contains(marker.getPosition()),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInfoWindowClose(Marker marker) {

    }

    @Override
    public void onInfoWindowLongClick(Marker marker) {
//        if(DataLoader.userLatLng.contains(marker.getPosition())) {
//            int position = DataLoader.userLatLng.indexOf(marker.getPosition());
//            DataLoader.currentMarker = position;
//            //CustomExitDialog.detailsDialog = true;
//            //CustomExitDialog.existDialog = false;
//            DonorBasicInfo dialog = new DonorBasicInfo();
//            dialog.show(getFragmentManager(),"marker"+position);
//            //L.t(SearchDonor.this,"Position: "+AllStaticVar.userDetails.get(position).toString());
//        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //Toast.makeText(getApplicationContext(),"onMarkerClick "+DataLoader.userLatLng.contains(marker.getPosition()),Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onClick(View v) {
        v.startAnimation(DataLoader.buttonClick);
        int id = v.getId();
        switch (id){
            case R.id.blood_request:
                new AllDialog(this).showDialog("Blood Request", "Nearest " + totalDonorsSent + " donors will be notified about your blood request. Do you want to continue?"
                        , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                DataLoader.context = SearchDonorFragment.this;
                                DataLoader.profileInfo = null;
                                DataLoader.getUserFromServer("BloodRequest");

                            }
                        },"Send","No");


                break;
        }


    }

    @Override
    public void onLocationChanged(Location location) {

    }

    /** Demonstrates customizing the info window and/or its contents. */
    class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        // These are both viewgroups containing an ImageView with id "badge" and two TextViews with id
        // "title" and "snippet".
        private final View mWindow;

        private final View mContents;

        CustomInfoWindowAdapter() {
            mWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);
            mContents = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {

            render(marker, mWindow);
            return mWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            render(marker, mContents);
            return mContents;
        }

        private void render(Marker marker, View view) {
            int badge = R.mipmap.ic_launcher;

            ((ImageView) view.findViewById(R.id.badge)).setImageResource(badge);

            String title = marker.getTitle();
            TextView titleUi = ((TextView) view.findViewById(R.id.title));
            if (title != null) {
                // Spannable string allows us to edit the formatting of the text.
                SpannableString titleText = new SpannableString(title);
                titleText.setSpan(new ForegroundColorSpan(Color.RED), 0, titleText.length(), 0);
                titleUi.setText(titleText);
            } else {
                titleUi.setText("");
            }

            String snippet = marker.getSnippet();
            TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
            if (snippet != null && snippet.length() > 12) {
                SpannableString snippetText = new SpannableString(snippet);
                snippetText.setSpan(new ForegroundColorSpan(Color.MAGENTA), 0, 10, 0);
                snippetText.setSpan(new ForegroundColorSpan(Color.BLUE), 12, snippet.length(), 0);
                snippetUi.setText(snippetText);
            } else {
                snippetUi.setText("");
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_donor_fragment);

        manager = getFragmentManager();

        dialog = new AllDialog(this).showProgressDialog("","Searching nearest Blood Donors...",true,true);

        dialog.show();

        blood_request = (AppCompatButton) findViewById(R.id.blood_request);

        blood_request.setOnClickListener(this);

        context = this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        markerLocation = new HashMap<>();

        //Check if Google Play Services Available or not
        if (!CheckGooglePlayServices()) {
            //Log.d("onCreate", "Finishing test case since Google Play Services are not available");
            //finish();
        }
        else {
            //Log.d("onCreate","Google Play Services available.");
        }

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        SetToolbar.context = this;
        getSupportActionBar().setTitle(SetToolbar.setTitle());
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(SetToolbar.setBgColor()));

        NavigationDrawerFragment drawerFragment =
                (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_drawer);
        drawerFragment.setUp(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);


        SupportMapFragment map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        map.getMapAsync(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        displayLocationSettingsRequest(this);

        DataLoader.checkLogin(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id){

            case R.id.action_logout:

                DataLoader.context = this;
                DataLoader.removeLocalVars();

                Intent intent = new Intent(this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;

        }
        return true;
    }




    private void displayLocationSettingsRequest(Context context) {

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
                //startLocationUpdates();
                //checkLocationPermission();

            }

        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {

                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        dialog.hide();
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(SearchDonorFragment.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });

        /*GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
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
                        //Log.i(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        //Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            dialog.hide();
                            status.startResolutionForResult(SearchDonorFragment.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            //Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        //Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Log.d("onActivityResult()", Integer.toString(resultCode));

        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        removeAndAddFragments();
                        // All required changes were successfully made
                        //Toast.makeText(getActivity(), "Location enabled by user!", Toast.LENGTH_LONG).show();
                        break;
                    }
                    case Activity.RESULT_CANCELED: {
                        // The user was asked to change settings, but chose not to
                        //Toast.makeText(getActivity(), "Location not enabled, user cancelled.", Toast.LENGTH_LONG).show();
                        removeAndAddFragments();
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
        }
    }

    public void showPlacesInfo() {

        String url = getString(R.string.server_url)+"retreive_nearest_donors.php";

        DataLoader.context = this;
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                dialog.hide();
                try {
                    DataLoader.userDetails =  new ArrayList<>();
                    DataLoader.userLatLng =  new ArrayList<>();
                    DataLoader.locCheck =  new ArrayList<>();
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject mJsonObject;

                    if(jsonArray.length() <= 3){
                        totalDonorsSent = jsonArray.length();
                    }else{
                        totalDonorsSent = 3;
                    }

                    if(jsonArray.length() == 0){

                        new AllDialog(SearchDonorFragment.this).showDialog("Not found","No Donors found. Try with different search.",null,null,"Ok");

                    }

                    for (int i = 0; i < jsonArray.length(); i++) {

                        mJsonObject = jsonArray.getJSONObject(i);
                        DataLoader.UserInfo userInfo = new DataLoader.UserInfo();

                        userInfo.phone = mJsonObject.getString("phone");
                        userInfo.pic_path = mJsonObject.getString("pic_path");
                        userInfo.fname = mJsonObject.getString("fname");
                        userInfo.lname = mJsonObject.getString("lname");
                        userInfo.blood_group = mJsonObject.getString("blood_group");
                        userInfo.birth_date = mJsonObject.getString("birth_date");
                        userInfo.age = mJsonObject.getString("age");
                        userInfo.last_donation = mJsonObject.getString("last_donation");
                        userInfo.new_donor = mJsonObject.getString("new_donor");
                        userInfo.email = mJsonObject.getString("email");
                        userInfo.division = mJsonObject.getString("division");
                        userInfo.district = mJsonObject.getString("district");
                        userInfo.upazila = mJsonObject.getString("upazila");

                        userInfo.address = mJsonObject.getString("address");
                        userInfo.fcm_email = mJsonObject.getString("fcm_email");
                        userInfo.fcm_uid = mJsonObject.getString("fcm_uid");
                        userInfo.fcm_token = mJsonObject.getString("fcm_token");


                        userInfo.pro_visible = mJsonObject.getString("pro_visible");
                        userInfo.called_date = mJsonObject.getString("called_date");
                        userInfo.called_today = mJsonObject.getString("called_today");
                        userInfo.donations_number = mJsonObject.getString("donations_number");
                        userInfo.user_type = mJsonObject.getString("user_type");
                        userInfo.gender = mJsonObject.getString("gender");
                        userInfo.already_donated = mJsonObject.getString("already_donated");
                        userInfo.autopro_visible = mJsonObject.getString("autopro_visible");


                        //Place current location marker
                        Double lat = 0.0, lng = 0.0;
                        if(mJsonObject.getString("lastLat").equals("null") ||
                                mJsonObject.getString("lastLat").equals("na") ||
                                mJsonObject.getString("lastLng").equals("null") ||
                                mJsonObject.getString("lastLng").equals("na")){

                            lat = 0.0;
                            lng = 0.0;

                        }else {
                            try {
                                lat = Double.parseDouble(mJsonObject.getString("lastLat"));
                                lng = Double.parseDouble(mJsonObject.getString("lastLng"));
                            }catch(Exception e){

                            }
                        }




                        LatLng latLng = new LatLng(lat, lng);


                        String check = lat+""+lng;
                        if(i != 0 && DataLoader.locCheck.contains(lat+""+lng)){
                            Random rand = new Random();
                            double offset = i / 2000d;
                            //double offset = 0.00005d;
                            lat = lat + offset;
                            lng = lng + offset;
                            DataLoader.locCheck.add(check);
                            latLng = new LatLng(lat, lng);
                        }else if(i == 0){
                            DataLoader.locCheck.add(lat+""+lng);
                        }

                        DataLoader.userLatLng.add(latLng);

                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title(userInfo.fname+" "+userInfo.lname);
                        markerOptions.snippet(userInfo.address);
                        switch (userInfo.blood_group){
                            case "A+":
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.apositive));
                                break;
                            case "A-":
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.anegative));
                                break;
                            case "B+":
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.bpositive));
                                break;
                            case "B-":
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.bnegative));
                                break;
                            case "O+":
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.opositive));
                                break;
                            case "O-":
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.onegative));
                                break;
                            case "AB+":
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.abpositive));
                                break;
                            case "AB-":
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.abnegative));
                                break;
                            default:
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                                break;

                        }
                        mMap.addMarker(markerOptions);
                        //mCurrLocationMarker.showInfoWindow();

                        userInfo.latitude = lat;
                        userInfo.longitude = lng;

                        DataLoader.userDetails.add(userInfo);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.hide();
                Toast.makeText(SearchDonorFragment.this,"Network Error. Please try again.",Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("phone", DataLoader.getUserPhone());
                params.put("filterBlood", SearchByLocation.searchByLocBlood);
                params.put("filterMile", "" + SearchByLocation.searchByLocMile);
                params.put("latitude", "" + latitude);
                params.put("longitude", "" + longitude);

                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(this).addToRequest(stringRequest);
    }

    public static void sendRequest(){


            JSONObject blood_request = new JSONObject();
            try {
                blood_request.put("requester_phone", DataLoader.getUserPhone());
                blood_request.put("requested_blood", SearchByLocation.searchByLocBlood);
                blood_request.put("bags", SearchByLocation.total_bags);
                blood_request.put("hospital", SearchByLocation.hospital);
                blood_request.put("address", SearchByLocation.request_address);

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            try {

                for (int i = 0; i < totalDonorsSent; i++) {

                    String username = DataLoader.profileInfo.fname + " " + DataLoader.profileInfo.lname;

                    String message = blood_request.toString();
                    String uid = DataLoader.profileInfo.fcm_uid;
                    String firebaseToken = DataLoader.profileInfo.fcm_token;
                    String receiverFirebaseToken = DataLoader.userDetails.get(i).fcm_token;

                    FcmNotificationBuilder.initialize()
                            .title("Blood Request")
                            .message(message)
                            .username(username)
                            .uid(uid)
                            .firebaseToken(firebaseToken)
                            .receiverFirebaseToken(receiverFirebaseToken)
                            .send();
                }

            } catch (Exception e) {

            }


    }

    public static void updateBloodRequest() {
        request_to = "";
        try {
            totalDonorsSent = 0;
            if(DataLoader.userDetails != null) {
                totalDonorsSent = DataLoader.userDetails.size();
                if(totalDonorsSent >= 3)
                    totalDonorsSent = 3;
                else
                    totalDonorsSent = DataLoader.userDetails.size();
            }
            for (int i = 0; i < totalDonorsSent; i++) {

                request_to += DataLoader.userDetails.get(i).phone;
                if (i != totalDonorsSent-1) {
                    request_to += ",";
                }

            }

        }catch (Exception e){

        }


        DataLoader.context = context;
        String app_server_url = context.getString(R.string.server_url) + "insert_blood_requests.php";

        try {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, app_server_url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //Toast.makeText(context, response, Toast.LENGTH_LONG).show();
                            if(response.equals("success")){

                                if(totalDonorsSent != 0) {
                                    sendRequest();
                                    //Toast.makeText(context, response, Toast.LENGTH_LONG).show();
                                    new AllDialog(context).showDialog("Congratulations !", "Your request has been sent to nearest donors. " +
                                            "LifeCycle will notify you when someone accepts your request. Thanks", null, null, "OK");

                                }else{
                                    new AllDialog(context).showDialog("Not Sent !", "No donors found. Please try with other options.", null, null, "OK");
                                }
                            }else if(response.equals("managed")){
                                new AllDialog(context).showDialog("","Sorry ! You can't send any more requests today. ",null,null,"OK");

                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //Log.d("insert_blood_requests",error.toString());
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("request_phone", DataLoader.getUserPhone());
                    params.put("request_blood", SearchByLocation.searchByLocBlood);
                    params.put("blood_bags", SearchByLocation.total_bags);
                    params.put("request_address", DataLoader.profileInfo.address);
                    params.put("request_hospital", SearchByLocation.hospital);
                    params.put("request_to", request_to);
                    params.put("request_time", getTime("request_time"));
                    params.put("deletion_date", getTime("deletion_date"));
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MySingleton.getmInstance(context).addToRequest(stringRequest);
        }catch (Exception e){
            Toast.makeText(context, ""+e, Toast.LENGTH_LONG).show();
        }
    }

    public static String getTime(String type){
        if(type.equals("request_time")){
            return ""+(System.currentTimeMillis()/1000L);
        }else if(type.equals("deletion_date")) {

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
            c.add(Calendar.DATE, 3);
            return "" + (c.getTimeInMillis() / 1000L);
        }
//        sdf = new SimpleDateFormat("dd-MM-yyyy");
//        Date resultdate = new Date(c.getTimeInMillis());
//        dateInString = sdf.format(resultdate);

        return "";
    }

    // Check if any marker is displayed on given coordinate. If yes then decide
// another appropriate coordinate to display this marker. It returns an
// array with latitude(at index 0) and longitude(at index 1).
    private String[] coordinateForMarker(float latitude, float longitude) {

        String[] location = new String[2];

        for (int i = 0; i <= MAX_NUMBER_OF_MARKERS; i++) {

            if (mapAlreadyHasMarkerForLocation((latitude + i
                    * COORDINATE_OFFSET)
                    + "," + (longitude + i * COORDINATE_OFFSET))) {

                // If i = 0 then below if condition is same as upper one. Hence, no need to execute below if condition.
                if (i == 0)
                    continue;

                if (mapAlreadyHasMarkerForLocation((latitude - i
                        * COORDINATE_OFFSET)
                        + "," + (longitude - i * COORDINATE_OFFSET))) {

                    continue;

                } else {
                    location[0] = latitude - (i * COORDINATE_OFFSET) + "";
                    location[1] = longitude - (i * COORDINATE_OFFSET) + "";
                    break;
                }

            } else {
                location[0] = latitude + (i * COORDINATE_OFFSET) + "";
                location[1] = longitude + (i * COORDINATE_OFFSET) + "";
                break;
            }
        }

        return location;
    }

    // Return whether marker with same location is already on map
    private boolean mapAlreadyHasMarkerForLocation(String location) {
        return (markerLocation.containsValue(location));
    }

    private boolean CheckGooglePlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        0).show();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Log.d("Anis","onMapReady is called");
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        // Setting an info window adapter allows us to change the both the contents and look of the
        // info window.
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());

        // Set listeners for marker events.  See the bottom of this class for their behavior.
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnInfoWindowCloseListener(this);
        mMap.setOnInfoWindowLongClickListener(this);

        getUserLocation();
        /*//Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }*/

    }

    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations, this can be null.
                        if (location != null) {
                            // Logic to handle location object

                            loadDonors(location);
                        }else {
                            dialog.hide();
                            Toast.makeText(SearchDonorFragment.this,"Couldn't detect your current location. Please try again.",Toast.LENGTH_LONG).show();
                            //removeAndAddFragments();
                        }
                    }
                });
    }

    /*protected synchronized void buildGoogleApiClient() {
        //Log.d("Anis","buildGoogleApiClient is called");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        //Log.d("Anis","onConnected is called");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            //LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }else{
            //Log.d("Anis","permission not granted");
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            //Toast.makeText(getActivity(),location.getLatitude()+" "+location.getLongitude(),Toast.LENGTH_LONG).show();
                            loadDonors(location);
                        }else {
                            dialog.hide();
                            Toast.makeText(SearchDonorFragment.this,"Couldn't detect your current location. Please try again.",Toast.LENGTH_LONG).show();
                            //removeAndAddFragments();
                        }

                    }
                });
    }

    //}

    @Override
    public void onConnectionSuspended(int i) {
        //Log.d("Anis","onConnectionSuspended is called");
    }*/


    public void loadDonors(Location location) {
//        Log.d("Anis","onLocationChanged is called");
//        Log.d("onLocationChanged", "entered");

        mLastLocation = location;
        updateLocation = location;
        try {
            //Log.d("locationStatus", "onLocationChanged = "+location.toString());
            if (mCurrLocationMarker != null) {
                mCurrLocationMarker.remove();
            }

            if (!SearchByLocation.useCurrentLoc) {
                latitude = SearchByLocation.searchByLocLatLng.latitude;
                longitude = SearchByLocation.searchByLocLatLng.longitude;
            } else {
                //Place current location marker
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        }catch (Exception e){

        }


        LatLng latLng = new LatLng(latitude, longitude);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);

        Geocoder gc = new Geocoder(this, Locale.getDefault());
        String address = "";
        try {
            List<android.location.Address> list = gc.getFromLocation(latitude,longitude,1);
            address = list.get(0).getAddressLine(0);
            //locality = add.getLocality();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(SearchByLocation.useCurrentLoc){
            SearchByLocation.request_address = address;
        }


        markerOptions.title("You: "+address);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
        mCurrLocationMarker.showInfoWindow();
        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
        //Toast.makeText(this,"Your Current Location", Toast.LENGTH_LONG).show();

        showPlacesInfo();

    }

    /*@Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //Log.d("Anis","onConnectionFailed is called");
    }*/

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        //Log.d("Anis","checkLocationPermission is called");
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        //Log.d("Anis","onRequestPermissionsResult is called");
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //removeAndAddFragments();
                        /*if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);*/
                    }

                } else {
                    //removeAndAddFragments();
                    // Permission denied, Disable the functionality that depends on this permission.
                    //Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    private void removeAndAddFragments() {
        //Log.d("SearchDonorFragment","Inside removeAndAddFragments");
        startActivity(new Intent(this,SearchDonorFragment.class));
        finish();

    }


}