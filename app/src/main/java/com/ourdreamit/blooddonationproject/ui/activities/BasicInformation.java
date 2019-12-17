package com.ourdreamit.blooddonationproject.ui.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;
import androidx.appcompat.app.AppCompatActivity;
import android.text.InputType;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
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
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ourdreamit.blooddonationproject.utils.Constants;
import com.ourdreamit.blooddonationproject.utils.DataLoader;
import com.ourdreamit.blooddonationproject.utils.GetImagePath;
import com.ourdreamit.blooddonationproject.utils.ImageConverter;
import com.ourdreamit.blooddonationproject.utils.MySingleton;
import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.core.login.LoginContract;
import com.ourdreamit.blooddonationproject.core.login.LoginPresenter;
import com.ourdreamit.blooddonationproject.core.registration.RegisterContract;
import com.ourdreamit.blooddonationproject.core.registration.RegisterPresenter;
import com.ourdreamit.blooddonationproject.core.users.add.AddUserContract;
import com.ourdreamit.blooddonationproject.core.users.add.AddUserPresenter;
import com.ourdreamit.blooddonationproject.dialogs.AllDialog;
import com.ourdreamit.blooddonationproject.dialogs.SetLocation;
import com.ourdreamit.blooddonationproject.fcm.DeleteTokenService;
import com.ourdreamit.blooddonationproject.utils.SharedPrefUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
//import static com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage;

public class BasicInformation extends AppCompatActivity implements LoginContract.View,
        View.OnClickListener,
SetLocation.LocationSelectionListener,
        RegisterContract.View, AddUserContract.View,
        DataLoader.TalkToProfileDetails, View.OnTouchListener {
    ImageView pro_pic, clockwise;
    TextView birthDate, setLocation;
    public static TextView userAddress;
    private EditText firstName, lastName,bloodGroup, setBDate, lastDonation,gender,division,district,thana, email, password;
    public static final int IMAGE_GALLERY_REQUEST = 1;
    //Spinner  gender, division, district, thana;
    private DatePickerDialog dateDialog;
    private SimpleDateFormat dateFormatter;
    private String donorBlood = "Blood Group*";
    private String userGender = "Gender*";
    private String uDivision = "Division*";
    private String uDistrict = "District*";
    private String uThana = "Upazila*";
    private String newDonor;
    static ProgressDialog progressDialog;

    public String pic_name;
    public static String address;
    public static double latitude, longitude;
    String imageEncoded;


    //private Toolbar toolbar;
    Boolean updatePhotoClicked;

    public static String divisionList[],districtList[], thanaList[];
    String genderList[] = {"Male", "Female"};

    FragmentManager manager;

    Button updateProfile;
    CheckBox donorStatus;

    int age;

    Bitmap circularBitmap;
    String last_donation;
    String mCurrentPhotoPath;




    private RegisterPresenter mRegisterPresenter;
    private AddUserPresenter mAddUserPresenter;
    private LoginPresenter mLoginPresenter;
    //static ProgressDialog mProgressDialog;
    static Context context;
    Boolean locationOk;

    //GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    private static final String TAG = "BasicInformation";
    private static final int REQUEST_CHECK_SETTINGS = 1;
    ScrollView root;

    String[] bloodList;

    FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basicinformation);

        root = (ScrollView) findViewById(R.id.root);
        root.setOnTouchListener(this);

        locationOk = false;
        //SetLocation.fromSetLocaion = false;

        mRegisterPresenter = new RegisterPresenter(this);
        mAddUserPresenter = new AddUserPresenter(this);
        mLoginPresenter = new LoginPresenter(this);
        context = this;

        manager = getFragmentManager();
        updatePhotoClicked = false;

        donorStatus = (CheckBox) findViewById(R.id.donorStatus);
        donorStatus.setOnClickListener(this);
        donorStatus.setOnTouchListener(this);
        if (donorStatus.isChecked()) {
            donorStatus.setChecked(false);
        }


        pro_pic = (ImageView) findViewById(R.id.pro_pic);
        pro_pic.setOnClickListener(this);
        pro_pic.setOnTouchListener(this);

        setBDate = (EditText) findViewById(R.id.setBDate);
        // Set the EditText input type null
        setBDate.setInputType(InputType.TYPE_NULL);
        setBDate.setOnClickListener(this);
        setBDate.setOnTouchListener(this);
        clockwise = (ImageView) findViewById(R.id.clockwise);
        clockwise.setOnClickListener(this);
        clockwise.setOnTouchListener(this);
        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        //phone = (EditText) view.findViewById(R.id.phone);
        //birthDate = (TextView) findViewById(R.id.birthDate);
        userAddress = (TextView) findViewById(R.id.userAddress);
        //userAddress.setVisibility(View.GONE);
        address = "na";
//        setProPic = (TextView) findViewById(R.id.setProPic);
//        setProPic.setOnClickListener(this);
//        setProPic.setOnTouchListener(this);

        setLocation = (TextView) findViewById(R.id.setLocation);
        setLocation.setOnClickListener(this);
        setLocation.setOnTouchListener(this);

//        setlastDonation = (ImageView) findViewById(R.id.setlastDonation);
//        setlastDonation.setOnClickListener(this);
//        setlastDonation.setOnTouchListener(this);

        lastDonation = (EditText) findViewById(R.id.lastDonation);
        lastDonation.setInputType(InputType.TYPE_NULL);
        lastDonation.setOnClickListener(this);
        lastDonation.setOnTouchListener(this);

        updateProfile = (Button) findViewById(R.id.updateProfile);
        updateProfile.setOnClickListener(this);
        updateProfile.setOnTouchListener(this);

        progressDialog = new AllDialog(this).showProgressDialog("Saving...",
                getString(R.string.please_wait),false,false);
//        dialog.setMessage("Saving...");
//        dialog.setCancelable(false);
//        //dialog.setInverseBackgroundForced(true);
//        dialog.setCanceledOnTouchOutside(false);


        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        bloodGroup = (EditText) findViewById(R.id.bloodGroup);
        bloodGroup.setInputType(InputType.TYPE_NULL);
        bloodGroup.setOnClickListener(this);
        bloodGroup.setOnTouchListener(this);

        bloodList = getResources().getStringArray(R.array.blood_group);

        gender = (EditText) findViewById(R.id.gender);
        gender.setInputType(InputType.TYPE_NULL);
        gender.setOnClickListener(this);
        gender.setOnTouchListener(this);

        division = (EditText) findViewById(R.id.division);
        division.setInputType(InputType.TYPE_NULL);
        division.setOnClickListener(this);
        division.setOnTouchListener(this);

        divisionList = getResources().getStringArray(R.array.divsion_list);

        district = (EditText) findViewById(R.id.district);
        district.setInputType(InputType.TYPE_NULL);
        district.setOnClickListener(this);
        district.setOnTouchListener(this);

        thana = (EditText) findViewById(R.id.thana);
        thana.setInputType(InputType.TYPE_NULL);
        thana.setOnClickListener(this);
        thana.setOnTouchListener(this);


        /*// Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }*/



        //Check if Google Play Services Available or not
        if (!CheckGooglePlayServices()) {
            //Log.d("onCreate", "Finishing test case since Google Play Services are not available");
            //finish();
        } else {
            //Log.d("onCreate", "Google Play Services available.");
        }

        displayLocationSettingsRequest(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        
        getUserLocation();


    }

    @Override
    public void onBackPressed() {
        try {
            new AllDialog(this).showDialog("", "Are you sure you want to cancel LifeCycle registration?", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(BasicInformation.this, Login.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }, "Yes", "No");
        }catch (Exception e){

        }

    }

    private void displayLocationSettingsRequest(Context context) {
        /*GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();*/

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
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(BasicInformation.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });

        /*LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
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
                            status.startResolutionForResult(BasicInformation.this, REQUEST_CHECK_SETTINGS);
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

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

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

    public static final int MY_PERMISSIONS_REQUEST_STORAGE = 100;

    public boolean checkStoragePermission() {
        //Log.d("Anis", "checkLocationPermission is called");
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_STORAGE);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_STORAGE);
            }
            return false;
        } else {
            updatePhotoClicked = true;
            setProPic();
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        //Log.d("Anis", "onRequestPermissionsResult is called");
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
                        /*if (!SetLocation.fromSetLocaion)
                            startActivity(new Intent(this, BasicInformation.class));*/
                        //displayLocationSettingsRequest(this);


                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(this,BasicInformation.class));
                }
                return;
            }

            case MY_PERMISSIONS_REQUEST_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                        updatePhotoClicked = true;
                        setProPic();
                        //startActivity(new Intent(this,BasicInformation.class));

                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    //Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                    //startActivity(new Intent(this,BasicInformation.class));
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }


    @Override
    public void onClick(View v) {
        v.startAnimation(DataLoader.buttonClick);
        int id = v.getId();
        switch (id) {
            case R.id.pro_pic:

                checkStoragePermission();

                break;
            case R.id.clockwise:
                circularBitmap = rotateImage(circularBitmap, 90);
                //imageEncoded = getStringImage(circularBitmap);
                pro_pic.setImageBitmap(circularBitmap);
                break;
            case R.id.bloodGroup:
                new AllDialog(this).showListDialog("Blood Group*", bloodList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        bloodGroup.setText(bloodList[which]);
                        donorBlood = bloodList[which];
                    }
                });
                break;
            case R.id.setBDate:
                setBDate("setBDate");
                break;
            case R.id.lastDonation:
                if (donorStatus.isChecked())
                    Toast.makeText(this,"You have set your status as new donor",Toast.LENGTH_SHORT).show();
                else
                    setBDate("setlastDonation");
                break;
            case R.id.donorStatus:
                if (donorStatus.isChecked())
                    lastDonation.setText("");
                break;
            case R.id.gender:
                new AllDialog(this).showListDialog("Gender*", genderList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gender.setText(genderList[which]);
                        userGender = genderList[which];
                    }
                });
                break;
            case R.id.division:
                new AllDialog(this).showListDialog("Division*", divisionList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        division.setText(divisionList[which]);
                        uDivision = divisionList[which];
                        progressDialog.setMessage("Loading districts of " + uDivision);
                        progressDialog.show();
                        setDistrictList();
                    }
                });
                break;
            case R.id.district:
                new AllDialog(this).showListDialog("District*", districtList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        district.setText(districtList[which]);
                        uDistrict = districtList[which];
                        progressDialog.setMessage("Loading upazillas of " + uDistrict);
                        progressDialog.show();
                        setThanaList();
                    }
                });
                break;
            case R.id.thana:
                new AllDialog(this).showListDialog("Upazila*", thanaList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        thana.setText(thanaList[which]);
                        uThana = thanaList[which];
                    }
                });
                break;

            case R.id.setLocation:
                SetLocation setLocation = new SetLocation();
                setLocation.show(manager, "setLocation");



                break;
            case R.id.updateProfile:
                updateProfile();
                break;
        }
    }





    /*@Override
    public void onStart() {
        //mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
        super.onStop();
    }*/

    public void setBDate(final String idName) {
        Calendar newCalendar = Calendar.getInstance();
        dateDialog = new DatePickerDialog(this, R.style.datepicker,new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                if (idName.equals("setBDate")) {
                    age = getAge(year, monthOfYear, dayOfMonth);
                    if (age < 18) {
                        new AllDialog(BasicInformation.this).showDialog("","You must be at least 18 years old to be a donor. " +
                                "However you still can enjoy the app.",null,null,"OK");

                    } else {
                        Toast.makeText(BasicInformation.this, age + " years old", Toast.LENGTH_LONG).show();
                    }

                    setBDate.setText(dateFormatter.format(newDate.getTime()));
                } else if (idName.equals("setlastDonation")) {
                    lastDonation.setText(dateFormatter.format(newDate.getTime()));
                }
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        dateDialog.show();
    }

    //Calculate the users age
    public int getAge(int DOByear, int DOBmonth, int DOBday) {

        int age;

        final Calendar calenderToday = Calendar.getInstance();
        int currentYear = calenderToday.get(Calendar.YEAR);
        int currentMonth = 1 + calenderToday.get(Calendar.MONTH);
        int todayDay = calenderToday.get(Calendar.DAY_OF_MONTH);

        age = currentYear - DOByear;

        if (DOBmonth > currentMonth) {
            --age;
        } else if (DOBmonth == currentMonth) {
            if (DOBday > todayDay) {
                --age;
            }
        }
        return age;
    }

    public void setProPic() {
        // invoke the image gallery using an implict intent.
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);

        // where do we want to find the data?
        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureDirectoryPath = pictureDirectory.getPath();
        // finally, get a URI representation
        Uri data = Uri.parse(pictureDirectoryPath);

        // set the data and type.  Get all image types.
        photoPickerIntent.setDataAndType(data, "image/*");

        // we will invoke this activity, and get something back from it.
        startActivityForResult(photoPickerIntent, IMAGE_GALLERY_REQUEST);
    }

    public void updateProfile() {
        DataLoader.context = this;
        String donorEmail = email.getText().toString();
        if (!DataLoader.checkInternet()) {
            new AllDialog(this).showDialog("","Please connect to Internet to setup your account",null,null,"OK");

        } else if (firstName.getText().toString().isEmpty() || lastName.getText().toString().isEmpty() ||
                donorBlood.equals("Blood Group*") || email.getText().toString().isEmpty() ||
                userGender.equals("Gender*") || password.getText().toString().isEmpty() ||
                uDivision.equals("Division*") || uDistrict.equals("District*") ||
                uThana.equals("Upazila*")) {
            new AllDialog(this).showDialog("","Please fill all the * marked fields",null,null,"OK");


        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(donorEmail).matches()) {
            new AllDialog(this).showDialog("","Invalid Email Address. Please provide a valid email.",null,null,"OK");

        } else if (password.getText().toString().length() < 6) {
            new AllDialog(this).showDialog("","Invalid Password. Password length should be at least 6 characters long.",null,null,"OK");

        }

        else {
            progressDialog.show();
            //AllStaticVar.context = (this);
            DataLoader.context = this;

            if (donorStatus.isChecked()) {
                newDonor = "1";
            } else {
                newDonor = "0";
            }


            if (!lastDonation.getText().toString().isEmpty()) {
                last_donation = DataLoader.formatDate(lastDonation.getText().toString());
            } else {
                last_donation = "na";
            }

            if (newDonor.equals("1")) {
                last_donation = "na";
            }


            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            pic_name = firstName.getText().toString() + timeStamp + ".jpg";

            mRegisterPresenter.register(BasicInformation.this, email.getText().toString(), password.getText().toString());

            progressDialog.setMessage("Creating LiveChat Credentials");
        }
    }

    private void updateBasicInfo() {
        progressDialog.setMessage("Saving basic Information");
        progressDialog.show();
        String app_server_url = getString(R.string.server_url) + "update_userinfo.php";
        DataLoader.imageEncoded = imageEncoded;
        DataLoader.pic_name = pic_name;
        DataLoader.new_pic_name = "na";
        DataLoader.manager = manager;
        DataLoader.uploadProfilePhoto();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, app_server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressDialog.dismiss();
                        if (response.equals("Successful")) {

                            Calendar cal = Calendar.getInstance();
                            int yy = cal.get(Calendar.YEAR);
                            int mm = 1 + cal.get(Calendar.MONTH);
                            int dd = cal.get(Calendar.DAY_OF_MONTH);
                            final String dateInString = dd + "-" + mm + "-" + yy;

                            new SharedPrefUtil(BasicInformation.this).saveString(DataLoader.PASSWORD, password.getText().toString());
                            new SharedPrefUtil(BasicInformation.this).saveString(DataLoader.USERTYPE, "donor");
                            new SharedPrefUtil(BasicInformation.this).saveString(DataLoader.BLOODGROUP, donorBlood);
                            new SharedPrefUtil(BasicInformation.this).saveString(DataLoader.LOGIN, "true");
                            new SharedPrefUtil(BasicInformation.this).saveString(DataLoader.LOGOUT, "false");

                            new SharedPrefUtil(BasicInformation.this).saveString(DataLoader.PROCHECKDATE, dateInString);
                            //new SharedPrefUtil(BasicInformation.this).saveInt(DataLoader.PROCHECKED, 0);

                            DataLoader.userPass = password.getText().toString();

                            Intent tokenRef = new Intent(BasicInformation.this, DeleteTokenService.class);
                            startService(tokenRef);

                            DataLoader.updateFcmInfo("BasicInformation");

                        } else {
                            Toast.makeText(BasicInformation.this, response, Toast.LENGTH_LONG).show();
                            //Log.d("BasicUpdateError",response);
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(BasicInformation.this, error.toString(), Toast.LENGTH_LONG).show();
                        //Log.d("signup_error", error.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("imageEncoded",imageEncoded);
                params.put("pic_name", pic_name);
                params.put("firstName", firstName.getText().toString());
                params.put("lastName", lastName.getText().toString());
                params.put("donorBlood", donorBlood);
                params.put("phone", DataLoader.getUserPhone());
                params.put("birthDate", DataLoader.formatDate(setBDate.getText().toString()));
                params.put("age", "" + age);
                params.put("last_donation", last_donation);
                params.put("new_donor", newDonor);
                params.put("email", email.getText().toString());
                params.put("password", password.getText().toString());
                params.put("division", uDivision);
                params.put("district", uDistrict);
                params.put("upazila", uThana);
                params.put("address", address);
                params.put("latitude", "" + latitude);
                params.put("longitude", "" + longitude);
                params.put("gender", userGender);
                //params.put("singup_steps","3");
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(this).addToRequest(stringRequest);
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = pro_pic.getWidth();
        int targetH = pro_pic.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        imageEncoded = getStringImage(bitmap);

        circularBitmap = ImageConverter.getRoundedCornerBitmap(bitmap, 100);
        pro_pic.setImageBitmap(bitmap);

        clockwise.setVisibility(View.VISIBLE);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.d(TAG, "onActivityResult: requestCode  "+requestCode+" resultCode "+resultCode);
        if (updatePhotoClicked && resultCode == RESULT_OK) {
            // if we are here, everything processed successfully.
            if (requestCode == IMAGE_GALLERY_REQUEST) {
                // if we are here, we are hearing back from the image gallery.

                // the address of the image on the SD Card.
                Uri imageUri = data.getData();
                //Bitmap bit = getBitmap(imageUri);
                //Bitmap bit = decodeSampledBitmapFromResource(imageUri.getPath(),100,100);


                // declare a stream to read the image data from the SD Card.
                //InputStream inputStream;

                // we are getting an input stream, based on the URI of the image.
                try {
                    //mCurrentPhotoPath = getRealPathFromURI(imageUri);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                        mCurrentPhotoPath = GetImagePath.getPath(this, imageUri);
                    else
                        mCurrentPhotoPath = getRealPathFromURI(imageUri);


                    if (mCurrentPhotoPath != null) {
                        //Log.d("proPhotoError", mCurrentPhotoPath);
                        setPic();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // show a message to the user indictating that the image is unavailable.
                    Toast.makeText(this, "Unable to open image", Toast.LENGTH_LONG).show();

                }

            }

            updatePhotoClicked = false;
        }

        if (requestCode == Constants.AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                //Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                //Toast.makeText(this, "Place: " + place.getName() + ", " + place.getId(), Toast.LENGTH_SHORT).show();
                latitude = place.getLatLng().latitude;
                longitude = place.getLatLng().longitude;
                address = place.getAddress();
                userAddress.setText(place.getAddress());
                userAddress.setVisibility(View.VISIBLE);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                //Log.i(TAG, status.getStatusMessage());
                //Log.d(TAG, "onActivityResult: "+status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
                //Log.d(TAG, "onActivityResult: Cancelled");
            }
        }

        if (requestCode == REQUEST_CHECK_SETTINGS) {
            checkLocationPermission();
            //startActivity(new Intent(this,BasicInformation.class));
        }
    }

    public void openLocationActivity() {
        //uLocation.setText("");

        // Set the fields to specify which types of place data to
// return after the user has made a selection.
        List<Place.Field> fields = Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG);

// Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)
                .setCountry("BD")
                .build(this);
        startActivityForResult(intent, Constants.AUTOCOMPLETE_REQUEST_CODE);
    }


    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }


    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }






    private void setDistrictList() {
        String app_server_url = getString(R.string.server_url) + "get_all_locations.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, app_server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progressDialog.dismiss();

                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject mJsonObject;
                            //profileInfo = new DataLoader.UserInfo();
                            districtList = new String[jsonArray.length()];
                            for (int i = 0; i < jsonArray.length(); i++) {
                                mJsonObject = jsonArray.getJSONObject(i);

                                districtList[i] = mJsonObject.getString("district");


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(BasicInformation.this, "Network Error", Toast.LENGTH_LONG).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("phoneverify","true");
                params.put("division", uDivision);
                params.put("district", "na");
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(this).addToRequest(stringRequest);

    }


    private void setThanaList() {
        String app_server_url = getString(R.string.server_url) + "get_all_locations.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, app_server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progressDialog.dismiss();

                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject mJsonObject;
                            //profileInfo = new DataLoader.UserInfo();
                            thanaList = new String[jsonArray.length()];
                            for (int i = 0; i < jsonArray.length(); i++) {
                                mJsonObject = jsonArray.getJSONObject(i);

                                thanaList[i] = mJsonObject.getString("thana");


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(BasicInformation.this, "Network Error", Toast.LENGTH_LONG).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("phoneverify","true");
                params.put("division", uDivision);
                params.put("district", uDistrict);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(this).addToRequest(stringRequest);


    }




    private void setHotlineNumbers() {
        String url = getString(R.string.server_url) + "get_admin_settings.php";

        DataLoader.context = this;
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //Log.d("get_doctor_admins", response);

                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject mJsonObject;
                    //Log.d("SearchDonorFragment","user size "+ jsonArray.length());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        mJsonObject = jsonArray.getJSONObject(i);

                        DataLoader.hotline = mJsonObject.getString("hotline_phone");
                        DataLoader.ambulane = mJsonObject.getString("ambulance_phone");

                        new SharedPrefUtil(BasicInformation.this).saveString(DataLoader.HOTLINE, DataLoader.hotline);
                        new SharedPrefUtil(BasicInformation.this).saveString(DataLoader.AMBULANCE, DataLoader.ambulane);

                    }
                    updateBasicInfo();



                } catch (JSONException e) {
                    //Log.d("get_doctor_admins", e.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(BasicInformation.this, "Couldn't read url", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("request_type", "read");
                params.put("update_hotline", "na");
                params.put("update_ambulance", "na");
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(this).addToRequest(stringRequest);
    }

    /*@Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
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


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }*/

    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(BasicInformation.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(BasicInformation.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                .addOnSuccessListener(BasicInformation.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations, this can be null.
                        if (location != null) {
                            // Logic to handle location object

                            Geocoder gc = new Geocoder(BasicInformation.this, Locale.getDefault());

                            try {
                                List<Address> list = gc.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                                if (list != null) {
                                    if (list.size() != 0) {
                                        address = list.get(0).getAddressLine(0);
                                        latitude = location.getLatitude();
                                        longitude = location.getLongitude();
                                        BasicInformation.userAddress.setText(address);
                                        BasicInformation.userAddress.setVisibility(View.VISIBLE);

                                    }
                                }

                                //Toast.makeText(SignUpForm.this, address + " " + location.getLatitude() + " " + location.getLongitude(), Toast.LENGTH_LONG).show();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }





    @Override
    public void onRegistrationSuccess(FirebaseUser firebaseUser) {
        //Log.d("ChatTrace", "RegisterFragment onRegistrationSuccess");
        progressDialog.setMessage(getString(R.string.adding_user_to_db));
        Toast.makeText(this, "LiveChat Registration Successful!", Toast.LENGTH_SHORT).show();
        mAddUserPresenter.addUser(this.getApplicationContext(), firebaseUser);
    }

    @Override
    public void onRegistrationFailure(String message) {
        //Log.d("ChatTrace", "RegisterFragment onRegistrationFailure");
        progressDialog.dismiss();
        progressDialog.setMessage(getString(R.string.please_wait));
        //Log.e(TAG, "onRegistrationFailure: " + message);
        Toast.makeText(this, "LiveChat Registration failed!+\n" + message, Toast.LENGTH_LONG).show();
        DataLoader.context = this;
        setHotlineNumbers();
    }

    @Override
    public void onAddUserSuccess(String message) {
        //Log.d("ChatTrace", "RegisterFragment onAddUserSuccess");
        progressDialog.setMessage("Saving user to Lifecycle");
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        DataLoader.context = this;

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null) {
            String currentUserId = mAuth.getCurrentUser().getUid();
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId).child("userState");
            DataLoader.updateUserStatus(mAuth, rootRef, "online");
        }

        setHotlineNumbers();

    }


    @Override
    public void onAddUserFailure(String message) {
        DataLoader.context = this;
        setHotlineNumbers();

        progressDialog.dismiss();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onLoginSuccess(String message) {
        DataLoader.livechatlogin = "true";
        new SharedPrefUtil(BasicInformation.this).saveString(DataLoader.LIVECHATLOGIN, "true");

        Toast.makeText(this, "Livechat logged in successfully", Toast.LENGTH_SHORT).show();
        enterHome();
    }

    private void enterHome() {
        progressDialog.dismiss();
        Intent intent = new Intent(BasicInformation.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onLoginFailure(String message) {

        Toast.makeText(this, "Error: " + message, Toast.LENGTH_SHORT).show();
        enterHome();
    }

    @Override
    public void setUserDetails() {
        //progressDialog.dismiss();
        progressDialog.setMessage("Loggin in Lifecycle livechat...");
        mLoginPresenter.login(BasicInformation.this, email.getText().toString(), password.getText().toString());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(v.getContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
        return false;
    }

    @Override
    public void returnedLocStatus() {
        openLocationActivity();
    }
}
