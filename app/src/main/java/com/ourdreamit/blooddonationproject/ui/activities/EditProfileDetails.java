package com.ourdreamit.blooddonationproject.ui.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.Toolbar;
import android.text.InputType;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.ourdreamit.blooddonationproject.dialogs.SetLocation;
import com.ourdreamit.blooddonationproject.utils.Constants;
import com.ourdreamit.blooddonationproject.utils.ImageConverter;
import com.ourdreamit.blooddonationproject.utils.MySingleton;
import com.ourdreamit.blooddonationproject.ui.fragments.NavigationDrawerFragment;
import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.utils.SetToolbar;
import com.ourdreamit.blooddonationproject.dialogs.AllDialog;
import com.ourdreamit.blooddonationproject.dialogs.SetEditProfileLocation;
import com.ourdreamit.blooddonationproject.utils.DataLoader;
import com.ourdreamit.blooddonationproject.utils.SharedPrefUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
//import static com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage;

public class EditProfileDetails extends AppCompatActivity implements View.OnClickListener,
        SetLocation.LocationSelectionListener, DataLoader.TalkToProfileDetails, View.OnTouchListener {
    ImageView pro_pic, clockwise;
    TextView setLocation;
    public static TextView userAddress;
    private EditText firstName, lastName, bloodGroup, setBDate, lastDonation, postalCode, nationality, religion, nid;
    public static final int IMAGE_GALLERY_REQUEST = 1;
    private EditText gender, physically_disable, division, district, thana;
    private DatePickerDialog dateDialog;
    private SimpleDateFormat dateFormatter;
    private String donorBlood = "Blood Group*", userGender = "Gender*", physicalStatus = "Physically disabled?", uDivision = "Division*",
            uDistrict = "District*", uThana = "Upazila*", newDonor;
    ProgressDialog progressDialog;

    public String pic_name;
    public static String address;
    public static double latitude, longitude;
    String imageEncoded;


    Boolean updatePhotoClicked;
    String mCurrentPhotoPath;

    public static String divisionList[], districtList[], thanaList[];
    String genderList[] = {"Male", "Female"};
    String choiceList[] = {"No", "Yes"};

    FragmentManager manager;

    AppCompatButton updateProfile;
    AppCompatCheckBox donorStatus;

    int age;

    Bitmap circularBitmap;
    String last_donation, bDate;

    private static final String TAG = "GPSTAG";
    private static final int REQUEST_CHECK_SETTINGS = 1;


    String[] bloodList;

    private Toolbar toolbar;
    ScrollView root;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_basicinformation);

        root = (ScrollView) findViewById(R.id.root);
        root.setOnTouchListener(this);

        manager = getFragmentManager();
        updatePhotoClicked = false;
        //Log.d("updatePhotoClicked","oncreate "+updatePhotoClicked);

        donorStatus = (AppCompatCheckBox) findViewById(R.id.donorStatus);
        if (donorStatus.isChecked()) {
            donorStatus.setChecked(false);
        }


        pro_pic = (ImageView) findViewById(R.id.pro_pic);
        pro_pic.setOnClickListener(this);
        pro_pic.setOnTouchListener(this);

        setBDate = (EditText) findViewById(R.id.setBDate);
        setBDate.setInputType(InputType.TYPE_NULL);
        setBDate.setOnClickListener(this);
        setBDate.setOnTouchListener(this);

        clockwise = (ImageView) findViewById(R.id.clockwise);
        clockwise.setOnClickListener(this);
        clockwise.setOnTouchListener(this);
        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);

        postalCode = (EditText) findViewById(R.id.postalCode);
        nationality = (EditText) findViewById(R.id.nationality);
        religion = (EditText) findViewById(R.id.religion);
        nid = (EditText) findViewById(R.id.nid);

        userAddress = (TextView) findViewById(R.id.userAddress);
        address = "na";


        setLocation = (TextView) findViewById(R.id.setLocation);
        setLocation.setOnClickListener(this);
        setLocation.setOnTouchListener(this);



        lastDonation = (EditText) findViewById(R.id.lastDonation);
        lastDonation.setInputType(InputType.TYPE_NULL);
        lastDonation.setOnClickListener(this);
        lastDonation.setOnTouchListener(this);

        updateProfile = (AppCompatButton) findViewById(R.id.updateProfile);
        updateProfile.setOnClickListener(this);
        updateProfile.setOnTouchListener(this);

        progressDialog = new AllDialog(this).showProgressDialog("Saving...",
                getString(R.string.please_wait), true, false);



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

        physically_disable = (EditText) findViewById(R.id.physically_disable);
        physically_disable.setInputType(InputType.TYPE_NULL);
        physically_disable.setOnClickListener(this);
        physically_disable.setOnTouchListener(this);

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



        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);

        setUserDetails();

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        SetToolbar.context = this;
        getSupportActionBar().setTitle(SetToolbar.setTitle());
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(SetToolbar.setBgColor()));

        NavigationDrawerFragment drawerFragment =
                (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_drawer);
        drawerFragment.setUp(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);


        DataLoader.checkLogin(this);
    }

    @Override
    public void onBackPressed() {
        try {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }catch (Exception e){

        }
    }

    @Override
    public void setUserDetails() {
        setProfilePic();
        clockwise.setVisibility(View.VISIBLE);

        firstName.setText(DataLoader.profileInfo.fname);
        lastName.setText(DataLoader.profileInfo.lname);


        donorBlood = DataLoader.profileInfo.blood_group;
        bloodGroup.setText(donorBlood);

        age = Integer.parseInt(DataLoader.profileInfo.age);

        bDate = DataLoader.formatDate(DataLoader.profileInfo.birth_date);
        setBDate.setText(bDate);

        if (!DataLoader.profileInfo.last_donation.equals("na")) {
            last_donation = DataLoader.formatDate(DataLoader.profileInfo.last_donation);
            lastDonation.setText(last_donation);
        }

        if (DataLoader.profileInfo.new_donor.equals("1")) {
            donorStatus.setChecked(true);
        } else {
            donorStatus.setChecked(false);
        }


        userGender = DataLoader.profileInfo.gender;
        gender.setText(userGender);

        if (!DataLoader.profileInfo.religion.equals("na")) {
            religion.setText(DataLoader.profileInfo.religion);
        }

        String physical_status = DataLoader.profileInfo.is_physically_disble;
        if (!physical_status.equals("na") && physical_status == "Yes") {
            physically_disable.setText(choiceList[0]);
        } else if (!physical_status.equals("na") && physical_status == "No") {
            physically_disable.setText(choiceList[1]);
        }

        if (!DataLoader.profileInfo.nationality.equals("na")) {
            nationality.setText(DataLoader.profileInfo.nationality);
        }
        if (!DataLoader.profileInfo.nid.equals("na")) {
            nid.setText(DataLoader.profileInfo.nid);
        }

        if (!DataLoader.profileInfo.post_code.equals("na")) {
            postalCode.setText(DataLoader.profileInfo.post_code);
        }



        uDivision = DataLoader.profileInfo.division;
        division.setText(uDivision);
        setDistrictList();


        uDistrict = DataLoader.profileInfo.district;
        district.setText(uDistrict);
        uThana = DataLoader.profileInfo.upazila;
        thana.setText(uThana);
        setThanaList();
        if (!DataLoader.profileInfo.address.equals("na")) {
            userAddress.setText(DataLoader.profileInfo.address);
            address = DataLoader.profileInfo.address;
        }
        latitude = DataLoader.profileInfo.latitude;
        longitude = DataLoader.profileInfo.longitude;



    }

    public void setProfilePic() {
        String url = DataLoader.profileInfo.pic_path;

        Glide.with(this).load(url)
                .apply(RequestOptions.placeholderOf(R.drawable.userpic_default))
                .override(100,100)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(pro_pic);

        /*ImageRequest imageRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                //DataLoader.imageEncoded = getStringImage(response);
                Bitmap bitmap = ImageConverter.getRoundedCornerBitmap(response, 100);
                circularBitmap = bitmap;
                pro_pic.setImageBitmap(bitmap);
            }
        }, 0, 0, ImageView.ScaleType.CENTER_CROP, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(ProfileDetails.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        MySingleton.getmInstance(this).addToRequest(imageRequest);*/
    }



    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;


    public static final int MY_PERMISSIONS_REQUEST_STORAGE = 100;

    public boolean checkStoragePermission() {
        //Log.d("updatePhotoClicked","checkStoragePermission "+updatePhotoClicked);
        //Log.d("Anis","checkLocationPermission is called");
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
        //Log.d("Anis","onRequestPermissionsResult is called");
        switch (requestCode) {
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
                //Log.d("updatePhotoClicked","OnrequestPermissionResult "+updatePhotoClicked);
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
                try {
                    if (circularBitmap != null)
                        circularBitmap = rotateImage(circularBitmap, 90);

                    imageEncoded = getStringImage(circularBitmap);
                    pro_pic.setImageBitmap(circularBitmap);
                }catch (Exception e){

                }
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
                setBDate("setlastDonation");
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
            case R.id.physically_disable:
                new AllDialog(this).showListDialog("Physically Disabled?", choiceList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        physically_disable.setText(choiceList[which]);
                        physicalStatus = choiceList[which];
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
                SetEditProfileLocation setLocation = new SetEditProfileLocation();
                setLocation.show(manager, "setLocation");
                break;
            case R.id.updateProfile:
                updateProfile();
                break;
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_logout:
                new SharedPrefUtil(this).saveString(DataLoader.LOGOUT, "true");
                Intent intent = new Intent(this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;

        }
        return true;
    }


    @Override
    public void onStart() {
        //mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        //mGoogleApiClient.disconnect();
        super.onStop();
    }

    public void setBDate(final String idName) {
        Calendar newCalendar = Calendar.getInstance();
        dateDialog = new DatePickerDialog(this, R.style.datepicker, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                if (idName.equals("setBDate")) {
                    age = getAge(year, monthOfYear, dayOfMonth);
                    if (age < 18) {
                        new AllDialog(EditProfileDetails.this).showDialog("","You must be at least 18 years old to be a donor. " +
                                "However you still can enjoy the app.",null,"OK",null);

                    } else {
                        Toast.makeText(EditProfileDetails.this, age + " years old", Toast.LENGTH_LONG).show();
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

        if (physicalStatus.equals("Physically disabled?")) {
            physicalStatus = "na";
        }

        if (!DataLoader.checkInternet()) {
            new AllDialog(this).showDialog("", "Please connect to Internet to setup your account", null, null, "OK");

        } else if (firstName.getText().toString().isEmpty() || lastName.getText().toString().isEmpty() ||
                donorBlood.equals("Blood Group*") || userGender.equals("Gender*") || uDivision.equals("Division*") || uDistrict.equals("District*") ||
                uThana.equals("Upazila*")) {
            new AllDialog(this).showDialog("", "Please fill all the * marked fields", null, null, "OK");


        } else {
            String app_server_url = getString(R.string.server_url) + "edit_userinfo.php";
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

            if (lastDonation.getText().toString().isEmpty() || newDonor == "1") {
                last_donation = "na";
            }
            final String uemail, unationality, ureligion, unid, upost_code;


            if (nationality.getText().toString().isEmpty()) {
                unationality = "na";
            } else {
                unationality = nationality.getText().toString();
            }

            if (religion.getText().toString().isEmpty()) {
                ureligion = "na";
            } else {
                ureligion = religion.getText().toString();
            }

            if (nid.getText().toString().isEmpty()) {
                unid = "na";
            } else {
                unid = nid.getText().toString();
            }

            if (postalCode.getText().toString().isEmpty()) {
                upost_code = "na";
            } else {
                upost_code = postalCode.getText().toString();
            }


            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            pic_name = firstName.getText().toString() + timeStamp + ".jpg";
            //Log.d("updatePhotoClicked","updateProfile "+updatePhotoClicked);
            if (updatePhotoClicked) {
                DataLoader.imageEncoded = imageEncoded;
                DataLoader.pic_name = DataLoader.profileInfo.pic_path;
                DataLoader.new_pic_name = pic_name;
                DataLoader.manager = manager;
                //Log.d("PhotoTrace","imageEncoded: "+imageEncoded);
                //Log.d("PhotoTrace","phone: "+DataLoader.getUserPhone()+" pic_name: "+DataLoader.pic_name+" new_pic_name: "+DataLoader.new_pic_name);
                DataLoader.uploadProfilePhoto();
            }


            StringRequest stringRequest = new StringRequest(Request.Method.POST, app_server_url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.hide();
                            if (response.equals("Successful")) {
                                new SharedPrefUtil(EditProfileDetails.this).saveString(DataLoader.BLOODGROUP, donorBlood);
                                Intent intent = new Intent(EditProfileDetails.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                Toast.makeText(EditProfileDetails.this, "Saved Successfully", Toast.LENGTH_LONG).show();
                                //}
                            } else {
                                //Toast.makeText(EditProfileDetails.this, response, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(EditProfileDetails.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                Toast.makeText(EditProfileDetails.this, "Saved Successfully", Toast.LENGTH_LONG).show();
                            }


                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(EditProfileDetails.this, error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    //params.put("imageEncoded",imageEncoded);
                    //params.put("pic_name",pic_name);
                    params.put("firstName", firstName.getText().toString());
                    params.put("lastName", lastName.getText().toString());
                    params.put("donorBlood", donorBlood);
                    params.put("phone", DataLoader.getUserPhone());
                    params.put("birthDate", DataLoader.formatDate(setBDate.getText().toString()));
                    params.put("age", "" + age);
                    params.put("last_donation", last_donation);
                    params.put("new_donor", newDonor);
                    params.put("gender", userGender);
                    //params.put("email", uemail);
                    params.put("nationality", unationality);
                    params.put("religion", ureligion);
                    params.put("is_physically_disble", physicalStatus);
                    params.put("nid", unid);
                    params.put("post_code", upost_code);
                    params.put("division", uDivision);
                    params.put("district", uDistrict);
                    params.put("thana", uThana);
                    params.put("address", address);
                    params.put("latitude", "" + latitude);
                    params.put("longitude", "" + longitude);
                    //params.put("singup_steps","3");
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MySingleton.getmInstance(this).addToRequest(stringRequest);
        }
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
        pro_pic.setImageBitmap(circularBitmap);
        updatePhotoClicked = true;

        //clockwise.setVisibility(View.VISIBLE);

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // if we are here, everything processed successfully.
            if (updatePhotoClicked && requestCode == IMAGE_GALLERY_REQUEST) {
                // if we are here, we are hearing back from the image gallery.

                // the address of the image on the SD Card.
                Uri imageUri = data.getData();
                //Bitmap bit = decodeSampledBitmapFromResource(imageUri.getPath(),100,100);
                //Bitmap bit = getBitmap(imageUri);

                // declare a stream to read the image data from the SD Card.
                //InputStream inputStream;

                // we are getting an input stream, based on the URI of the image.
                try {
                    mCurrentPhotoPath = getRealPathFromURI(imageUri);
                    if (mCurrentPhotoPath != null) {
                        //Log.d("proPhotoError",mCurrentPhotoPath);
                        setPic();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // show a message to the user indictating that the image is unavailable.
                    Toast.makeText(this, "Unable to open image", Toast.LENGTH_LONG).show();
                    //Log.d("proPhotoError",e.toString());
                    //Log.d("proPhotoError",mCurrentPhotoPath);
                }

            }

            //updatePhotoClicked = false;
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
                        Toast.makeText(EditProfileDetails.this, "Network Error", Toast.LENGTH_LONG).show();

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

        //district.setOnItemSelectedListener(this);
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
                        Toast.makeText(EditProfileDetails.this, "Network Error", Toast.LENGTH_LONG).show();

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



    /*@Override
    public void removeLocFrag() {
        android.app.FragmentTransaction autocomplete = getFragmentManager().beginTransaction();
        //autocomplete.remove(getFragmentManager().findFragmentById(R.id.autocomplete_fragment));
        autocomplete.commit();
    }*/


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
}
