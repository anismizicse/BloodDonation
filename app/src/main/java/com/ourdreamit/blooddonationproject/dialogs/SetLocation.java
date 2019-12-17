package com.ourdreamit.blooddonationproject.dialogs;

import android.Manifest;
import android.app.DialogFragment;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.ourdreamit.blooddonationproject.ui.activities.BasicInformation;
import com.ourdreamit.blooddonationproject.utils.DataLoader;
import com.ourdreamit.blooddonationproject.R;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by anismizi on 6/17/17.
 */

public class SetLocation extends DialogFragment implements View.OnClickListener {
    TextView newAddress, location_error;
    Button changeLocation, back;
    String address;
    String currentAddress, searchedAddress;
    Double lat1, lng1, lat2, lng2;
    //GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    CheckBox myLocation;
    LocationSelectionListener locationSelectionListener;
    Boolean userlocation = false;
    Boolean searchedlocation = false;

    private static final String TAG = "GPSTAG";
    private static final int REQUEST_CHECK_SETTINGS = 1;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    //public static Boolean fromSetLocaion;
    LinearLayout userCurrent;
    FusedLocationProviderClient mFusedLocationClient;

    RelativeLayout edit_location;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setlocation_dialog, null);

        userCurrent = (LinearLayout) view.findViewById(R.id.userCurrent);

        //fromSetLocaion = false;

        changeLocation = (Button) view.findViewById(R.id.changeLocation);
        changeLocation.setOnClickListener(this);
        //Retrieve the PlaceAutocompleteFragment.

        newAddress = (TextView) view.findViewById(R.id.newAddress);
        location_error = (TextView) view.findViewById(R.id.location_error);
        myLocation = (CheckBox) view.findViewById(R.id.myLocation);
        myLocation.setOnClickListener(this);

//        changeLocation = (Button) view.findViewById(R.id.changeLocation);
//        changeLocation.setOnClickListener(this);

        back = (Button) view.findViewById(R.id.back);
        back.setOnClickListener(this);

        edit_location = view.findViewById(R.id.edit_location);
        edit_location.setOnClickListener(this);


        //Check if Google Play Services Available or not
        if (!CheckGooglePlayServices()) {
            //Log.d("onCreate", "Finishing test case since Google Play Services are not available");
            //finish();
        } else {
            //Log.d("onCreate", "Google Play Services available.");
        }
        displayLocationSettingsRequest(getActivity());


        setCancelable(false);

        locationSelectionListener = (LocationSelectionListener) getActivity();
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        getUserLocation();

        return view;
    }

    @Override
    public void onClick(View v) {
        v.startAnimation(DataLoader.buttonClick);
        int id = v.getId();
        switch (id) {
            case R.id.edit_location:
                locationSelectionListener.returnedLocStatus();
                dismiss();
                break;
            case R.id.changeLocation:
                if (address != null || searchedAddress != null) {
                    location_error.setVisibility(View.GONE);
                    if (userlocation) {
                        BasicInformation.latitude = lat1;
                        BasicInformation.longitude = lng1;
                        BasicInformation.userAddress.setVisibility(View.VISIBLE);
                        BasicInformation.userAddress.setText(address);
                    } else if(searchedlocation) {
                        BasicInformation.latitude = lat2;
                        BasicInformation.longitude = lng2;
                        BasicInformation.userAddress.setVisibility(View.VISIBLE);
                        BasicInformation.userAddress.setText(searchedAddress);
                    }else {
                        location_error.setVisibility(View.VISIBLE);
                    }
                    dismiss();
                } else if (address == null) {
                    location_error.setVisibility(View.VISIBLE);
                }


                break;
            case R.id.myLocation:
                if (myLocation.isChecked())
                    userlocation = true;
                else
                    userlocation = false;
                break;
            case R.id.back:
                dismiss();

                break;
        }


    }



    private void displayLocationSettingsRequest(Context context) {
//        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
//                .addApi(LocationServices.API).build();
//        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(getActivity());
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
                //startLocationUpdates();
                checkLocationPermission();

            }

        });

        task.addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {

                        dismiss();
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(getActivity(),
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });

    }

    private boolean CheckGooglePlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(getActivity());
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(getActivity(), result,
                        0).show();
            }
            return false;
        }
        return true;
    }



    public boolean checkLocationPermission() {
        //Log.d("Anis", "checkLocationPermission is called");
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                dismiss();
                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
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
        //Log.d("Anis", "onRequestPermissionsResult is called");
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        dismiss();

//                        SetLocation setLocation = new SetLocation();
//                        setLocation.show(manager, "setLocation");

                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    //Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                    //startActivity(new Intent(this,BasicInformation.class));
                }
                return;
            }

        }
    }


    public interface RemoveLocFrag {
        void removeLocFrag();
    }

    public interface LocationSelectionListener {
        void returnedLocStatus();
    }




    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations, this can be null.
                        if (location != null) {
                            // Logic to handle location object

                            Geocoder gc = new Geocoder(getActivity(), Locale.getDefault());

                            try {
                                List<Address> list = gc.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                                if (list != null) {
                                    if (list.size() != 0) {
                                        address = list.get(0).getAddressLine(0);
                                        lat1 = location.getLatitude();
                                        lng1 = location.getLongitude();
                                        String currentAddress = "Address: " + address;
                                        newAddress.setText(currentAddress);
                                        newAddress.setVisibility(View.VISIBLE);

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

    /*@Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
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
        if (mLastLocation != null) {
            userCurrent.setVisibility(View.VISIBLE);
            lat1 = mLastLocation.getLatitude();
            lng1 = mLastLocation.getLongitude();
            //Toast.makeText(getActivity(),"lat1 "+lat1+" "+"lng1 "+lng1,Toast.LENGTH_LONG).show();
            //Toast.makeText(getActivity(),BasicInformation.latitude+" "+BasicInformation.longitude,Toast.LENGTH_LONG).show();

            Geocoder gc = new Geocoder(getActivity(), Locale.getDefault());

            try {
                List<Address> list = gc.getFromLocation(lat1, lng1, 1);
                address = list.get(0).getAddressLine(0);
                newAddress.setText("Address: " + address);
                newAddress.setVisibility(View.VISIBLE);
                //BasicInformation.userAddress.setVisibility(View.VISIBLE);
                //BasicInformation.userAddress.setText(address);
                //locality = add.getLocality();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(address == null){
            userCurrent.setVisibility(View.GONE);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onError(Status status) {

    }*/
}
