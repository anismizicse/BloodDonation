package com.ourdreamit.blooddonationproject.ui.fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.Nullable;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.dialogs.AllDialog;
import com.ourdreamit.blooddonationproject.ui.activities.MainActivity;
import com.ourdreamit.blooddonationproject.utils.Constants;
import com.ourdreamit.blooddonationproject.utils.DataLoader;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by anismizi on 5/9/17.
 */

public class SearchByLocation extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener, View.OnTouchListener {

    public String donorBlood = "Blood Group*", requester_address;

    LatLng latLng;
    EditText bloodGroup, mileRange, blood_bags, hospital_name;

    AppCompatCheckBox myLocation;
    AppCompatButton searchDonors, cancel;
    Boolean userlocation = false;

    ScrollView root;
    TextView newAddress;

    public static Boolean useCurrentLoc;
    public static LatLng searchByLocLatLng;
    public static String searchByLocBlood;
    public static int searchByLocMile;

    public static String hospital, total_bags, request_address;

    String[] bloodList;

    RelativeLayout edit_location;
    FusedLocationProviderClient mFusedLocationClient;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_by_location, container, false);

        root = (ScrollView) view.findViewById(R.id.root);
        root.setOnTouchListener(this);

        newAddress = (TextView) view.findViewById(R.id.newAddress);


        mileRange = (EditText) view.findViewById(R.id.mileRange);
        blood_bags = (EditText) view.findViewById(R.id.blood_bags);
        hospital_name = (EditText) view.findViewById(R.id.hospital_name);

        myLocation = (AppCompatCheckBox) view.findViewById(R.id.myLocation);
        searchDonors = (AppCompatButton) view.findViewById(R.id.searchDonors);
        cancel = (AppCompatButton) view.findViewById(R.id.cancel);

        myLocation.setOnClickListener(this);
        searchDonors.setOnClickListener(this);
        cancel.setOnClickListener(this);

        if (myLocation.isChecked())
            userlocation = true;
        else
            userlocation = false;

        bloodGroup = (EditText) view.findViewById(R.id.bloodGroup);
        bloodGroup.setInputType(InputType.TYPE_NULL);
        bloodGroup.setOnClickListener(this);
        bloodList = getResources().getStringArray(R.array.blood_group);

        edit_location = view.findViewById(R.id.edit_location);
        edit_location.setOnClickListener(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        getUserLocation();

        return view;
    }


    @Override
    public void onClick(View v) {
        try {
            DataLoader.context = getActivity();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(v.getContext().INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
        }catch (Exception e){

        }
        int miles;

        int id = v.getId();
        switch (id) {
            case R.id.bloodGroup:
                try {
                    new AllDialog(getActivity()).showListDialog("Blood Group*", bloodList, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            bloodGroup.setText(bloodList[which]);
                            donorBlood = bloodList[which];
                        }
                    });
                }catch (Exception e){

                }
            case R.id.myLocation:
                if (myLocation.isChecked())
                    userlocation = true;
                else
                    userlocation = false;
                break;
            case R.id.cancel:
                v.startAnimation(DataLoader.buttonClick);
                try {
                    startActivity(new Intent(getActivity(), MainActivity.class));
                }catch (Exception e){

                }
                break;
            case R.id.searchDonors:
                v.startAnimation(DataLoader.buttonClick);
                try {
                    if (donorBlood.equals("Blood Group*")) {
                        new AllDialog(getActivity()).showDialog("", "Please provide Blood Group.", null, null, "Ok");

                    } else if (mileRange.getText().toString().isEmpty()) {
                        new AllDialog(getActivity()).showDialog("", "Please provide distance. LifeCycle will seach donors within that distance from your" +
                                " preferred distance.", null, null, "Ok");

                    } else if (blood_bags.getText().toString().isEmpty()) {
                        new AllDialog(getActivity()).showDialog("", "Please provide how many bags of blood you need.", null, null, "Ok");

                    } else if (hospital_name.getText().toString().isEmpty()) {
                        new AllDialog(getActivity()).showDialog("", "Please provide the name of hospital where you need blood.", null, null, "Ok");

                    } else {

                        miles = Integer.parseInt(mileRange.getText().toString());
                        useCurrentLoc = userlocation;
                        searchByLocMile = miles;
                        searchByLocBlood = donorBlood;
                        searchByLocLatLng = latLng;

                        total_bags = blood_bags.getText().toString();
                        hospital = hospital_name.getText().toString();
                        request_address = requester_address;


                        if (!DataLoader.checkInternet()) {
                            new AllDialog(getActivity()).showDialog("", "Please connect to Internet.", null, null, "Ok");

                        } else {
                            startActivity(new Intent(getActivity(), SearchDonorFragment.class));
                        }

                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                }
                //dismiss();
                break;

            case R.id.edit_location:
                openLocationActivity();
                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                //Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                //Toast.makeText(this, "Place: " + place.getName() + ", " + place.getId(), Toast.LENGTH_SHORT).show();
                try {
                    latLng = place.getLatLng();
                    requester_address = place.getAddress().toString();
                    newAddress.setText(requester_address);
                    newAddress.setVisibility(View.VISIBLE);
                }catch (Exception e){

                }
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
                                        String address = list.get(0).getAddressLine(0);
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

    public void openLocationActivity() {
        //uLocation.setText("");

        // Set the fields to specify which types of place data to
// return after the user has made a selection.
        List<com.google.android.libraries.places.api.model.Place.Field> fields = Arrays.asList(
                com.google.android.libraries.places.api.model.Place.Field.ID,
                com.google.android.libraries.places.api.model.Place.Field.NAME,
                com.google.android.libraries.places.api.model.Place.Field.ADDRESS,
                com.google.android.libraries.places.api.model.Place.Field.LAT_LNG);

// Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)
                .setCountry("BD")
                .build(getActivity());
        startActivityForResult(intent, Constants.AUTOCOMPLETE_REQUEST_CODE);
    }



    @Override
    public void onResume() {
        super.onResume();
        if (myLocation.isChecked())
            userlocation = true;
        else
            userlocation = false;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Toast.makeText(getActivity(), "Selected " + parent.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
        donorBlood = getResources().getStringArray(R.array.blood_group)[position];

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        try {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(v.getContext().INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
        }catch (Exception e){

        }
        return false;
    }
}
