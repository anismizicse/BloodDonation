package com.ourdreamit.blooddonationproject.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

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
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.ourdreamit.blooddonationproject.utils.ImageConverter;
import com.ourdreamit.blooddonationproject.utils.MenuOptionsSelected;
import com.ourdreamit.blooddonationproject.utils.MySingleton;
import com.ourdreamit.blooddonationproject.ui.fragments.NavigationDrawerFragment;
import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.utils.Constants;
import com.ourdreamit.blooddonationproject.utils.DataLoader;
import com.ourdreamit.blooddonationproject.utils.SetToolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FirebaseProfileDetails extends AppCompatActivity implements View.OnClickListener /*OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener*/ {
    private Toolbar toolbar;
    ImageView pro_pic;
    TextView name, email,password, gender, birth_date, last_donation, phone, division, district, thana,postalCode,
            currentAddress, blood_group, /*called_today,*/ pro_visible, donations_number,
            religion,physically_disable,nationality,nid,rank;
    AppCompatButton back, edit;

    //private GoogleMap mMap;
    //Marker mCurrLocationMarker;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    //SupportMapFragment map;
    DataLoader.UserInfo profileInfo;
    Intent intent;
    String fcm_token,fcm_uid,senderName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firebase_profile_details);

        intent = getIntent();
        if (intent.hasExtra(Constants.ARG_FIREBASE_TOKEN)) {
            senderName = intent.getStringExtra(Constants.ARG_RECEIVER);
            fcm_uid = intent.getStringExtra(Constants.ARG_RECEIVER_UID);
            //Log.d("FirebaseProfileDetails", senderName);
            //receiverToken = intent.getStringExtra(Constants.ARG_FIREBASE_TOKEN);
        }

        pro_pic = (ImageView) findViewById(R.id.pro_pic);

        name = (TextView) findViewById(R.id.name);
        email = (TextView) findViewById(R.id.email);
        password = (TextView) findViewById(R.id.password);
        gender = (TextView) findViewById(R.id.gender);
        birth_date = (TextView) findViewById(R.id.birth_date);
        last_donation = (TextView) findViewById(R.id.last_donation);
        phone = (TextView) findViewById(R.id.phone);
        division = (TextView) findViewById(R.id.division);
        district = (TextView) findViewById(R.id.district);
        thana = (TextView) findViewById(R.id.thana);
        postalCode = (TextView) findViewById(R.id.postalCode);
        blood_group = (TextView) findViewById(R.id.blood_group);
        //called_today = (TextView) findViewById(R.id.called_today);
        pro_visible = (TextView) findViewById(R.id.pro_visible);
        donations_number = (TextView) findViewById(R.id.donations_number);
        currentAddress = (TextView) findViewById(R.id.currentAddress);

        religion = (TextView) findViewById(R.id.religion);
        physically_disable = (TextView) findViewById(R.id.physically_disable);
        nationality = (TextView) findViewById(R.id.nationality);
        nid = (TextView) findViewById(R.id.nid);
        rank = (TextView) findViewById(R.id.rank);

        //called_today.setVisibility(View.GONE);

        back = (AppCompatButton) findViewById(R.id.back);
        edit = (AppCompatButton) findViewById(R.id.edit);

        edit.setText("Call");

        back.setOnClickListener(this);
        edit.setOnClickListener(this);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        SetToolbar.context = this;
        getSupportActionBar().setTitle(SetToolbar.setTitle());
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(SetToolbar.setBgColor()));

        NavigationDrawerFragment drawerFragment =
                (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_drawer);
        drawerFragment.setUp(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);


        getUserFromServer();

        DataLoader.checkLogin(this);
    }

    private void getUserFromServer() {
        String app_server_url = getString(R.string.server_url) + "firebase_profile_details.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, app_server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(FirebaseProfileDetails.this,response + fcm_uid, Toast.LENGTH_LONG).show();


                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject mJsonObject;
                            profileInfo = new DataLoader.UserInfo();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                mJsonObject = jsonArray.getJSONObject(i);

                                //insert userinfo into userDetails list arrays

                                //pic_path,fname,lname,email,phone,birthDate,bloodgroup,location,latitude,longitude
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
                                profileInfo.latitude = Double.parseDouble(mJsonObject.getString("latitude"));
                                profileInfo.longitude = Double.parseDouble(mJsonObject.getString("longitude"));
                                profileInfo.code = mJsonObject.getString("code");
                                profileInfo.verification = mJsonObject.getString("verification");
                                profileInfo.lastLat = Double.parseDouble(mJsonObject.getString("lastLat"));
                                profileInfo.lastLng = Double.parseDouble(mJsonObject.getString("lastLng"));
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


                                //Log.d("FirebaseProfileDetails","Inside ");
                                setUserDetails();

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            //Log.d("FirebaseProfileDetails",e.toString());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(FirebaseProfileDetails.this, "Network Error", Toast.LENGTH_LONG).show();
                        //Log.d("FirebaseProfileDetails",error.toString());

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("phoneverify","true");
                params.put("fcm_uid", fcm_uid);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(this).addToRequest(stringRequest);
    }


    public void setUserDetails() {

        //map.getMapAsync(this);

        setProfilePic();

        name.setText(profileInfo.fname + " " + profileInfo.lname);
        email.setText(profileInfo.email);
        password.setText("***********");
        gender.setText(profileInfo.gender);
        birth_date.setText(DataLoader.formatDate(profileInfo.birth_date));
        //String donation = "na";
        if(!profileInfo.last_donation.equals("na")){
            last_donation.setText(DataLoader.formatDate(profileInfo.last_donation));
        }
        //last_donation.setText(donation);
        phone.setText(profileInfo.phone);
        division.setText(profileInfo.division);
        district.setText(profileInfo.district);
        thana.setText(profileInfo.upazila);
        blood_group.setText(profileInfo.blood_group);
        if(!profileInfo.post_code.equals("na")){
            postalCode.setText(profileInfo.post_code);
        }
        if(!profileInfo.address.equals("na")){
            currentAddress.setText(profileInfo.address);
        }


        if(!profileInfo.religion.equals("na")){
            religion.setText(profileInfo.religion);
        }
        if(!profileInfo.is_physically_disble.equals("na")){
            physically_disable.setText(profileInfo.is_physically_disble);

        }
        if(!profileInfo.nationality.equals("na")){
            nationality.setText(profileInfo.nationality);
        }
        if(!profileInfo.nid.equals("na")){
            nid.setText(profileInfo.nid);
        }
        //if(!DataLoader.profileInfo.rank.equals("na")){
        rank.setText(profileInfo.rank);

//        String called;
//        if(profileInfo.called_today.equals("0")) {
//            called = "No";
//        }
//        else {
//            called = "Yes";
//        }
//        called_today.setText(called);
        String profile;
        if(profileInfo.pro_visible.equals("0")) {
            profile = "No";
        }
        else {
            profile = "Yes";
        }
        pro_visible.setText(profile);
        donations_number.setText(profileInfo.donations_number);
    }

    public void setProfilePic() {
        String url = profileInfo.pic_path;

        Glide.with(this).load(url)
                .apply(RequestOptions.placeholderOf(R.drawable.userpic_default))
                .override(100,100)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(pro_pic);

        /*ImageRequest imageRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                Bitmap circularBitmap = ImageConverter.getRoundedCornerBitmap(response, 100);
                pro_pic.setImageBitmap(circularBitmap);
            }
        }, 0, 0, ImageView.ScaleType.CENTER_CROP, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(FirebaseProfileDetails.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        MySingleton.getmInstance(this).addToRequest(imageRequest);*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MenuOptionsSelected menuOptions = new MenuOptionsSelected();
        menuOptions.createMenuActions(item, this);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.back:
                finish();
                startActivity(new Intent(this,MainActivity.class));
                break;
            case R.id.edit:
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + profileInfo.phone));
                startActivity(intent);
                break;
        }
    }

    private boolean CheckGooglePlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        0).show();
            }
            return false;
        }
        return true;
    }

    public boolean checkLocationPermission() {
        //Log.d("Anis", "checkLocationPermission is called");
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

    /*@Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("Anis", "onMapReady is called");
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        //Place current location marker
        LatLng latLng = new LatLng(DataLoader.profileInfo.lastLat, DataLoader.profileInfo.lastLng);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);

        Geocoder gc = new Geocoder(this, Locale.getDefault());
        String address = "";
        try {
            List<Address> list = gc.getFromLocation(DataLoader.profileInfo.lastLat, DataLoader.profileInfo.lastLng, 1);
            address = list.get(0).getAddressLine(0);
            //locality = add.getLocality();
        } catch (IOException e) {
            e.printStackTrace();
        }

        markerOptions.title(address);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
        mCurrLocationMarker.showInfoWindow();
        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

//        //Initialize Google Play Services
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (ContextCompat.checkSelfPermission(this,
//                    android.Manifest.permission.ACCESS_FINE_LOCATION)
//                    == PackageManager.PERMISSION_GRANTED) {
//                buildGoogleApiClient();
//                mMap.setMyLocationEnabled(true);
//            }
//        } else {
//            buildGoogleApiClient();
//            mMap.setMyLocationEnabled(true);
//        }
    }

//    protected synchronized void buildGoogleApiClient() {
//        Log.d("Anis", "buildGoogleApiClient is called");
//        mGoogleApiClient = new GoogleApiClient.Builder(activity)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();
//        mGoogleApiClient.connect();
//    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }*/


}
