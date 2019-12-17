package com.ourdreamit.blooddonationproject.ui.activities;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.utils.SetToolbar;
import com.ourdreamit.blooddonationproject.dialogs.AllDialog;
import com.ourdreamit.blooddonationproject.ui.fragments.NavigationDrawerFragment;
import com.ourdreamit.blooddonationproject.utils.DataLoader;
import com.ourdreamit.blooddonationproject.utils.ImageConverter;
import com.ourdreamit.blooddonationproject.utils.MySingleton;
import com.ourdreamit.blooddonationproject.utils.SharedPrefUtil;

public class ProfileDetails extends AppCompatActivity implements View.OnClickListener, DataLoader.TalkToProfileDetails {
    private Toolbar toolbar;
    public static ProgressDialog dialog;
    ImageView pro_pic;
    TextView name, email,password, gender, birth_date, last_donation, phone,
            division, district, thana,postalCode,currentAddress, blood_group, pro_visible,
            donations_number,religion,physically_disable,nationality,nid,rank;
    AppCompatButton back, edit;
    ImageView locationMap;
    private static final String TAG = "ProfileDetails";

    LinearLayout donorAddress,donorLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_details);

        dialog = new AllDialog(this).showProgressDialog("",
                "Loading Profile Details...",true,true);
        dialog.show();

        pro_pic = (ImageView) findViewById(R.id.pro_pic);
        locationMap = (ImageView) findViewById(R.id.locationMap);

        donorAddress = (LinearLayout) findViewById(R.id.donorAddress);
        donorLocation = (LinearLayout) findViewById(R.id.donorLocation);

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


        back = (AppCompatButton) findViewById(R.id.back);
        edit = (AppCompatButton) findViewById(R.id.edit);

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


        DataLoader.context = this;
        DataLoader.profileInfo = null;
        DataLoader.getUserFromServer("ProfileDetails");

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
        dialog.hide();
        //map.getMapAsync(this);

        setProfilePic();

        name.setText(DataLoader.profileInfo.fname + " " + DataLoader.profileInfo.lname);
        //if(!DataLoader.profileInfo.email.equals("na")) {
            email.setText(DataLoader.profileInfo.email);
        //}
        password.setText("***********");
        gender.setText(DataLoader.profileInfo.gender);
        birth_date.setText(DataLoader.formatDate(DataLoader.profileInfo.birth_date));
        //String donation = "na";
        if(!DataLoader.profileInfo.last_donation.equals("na")){
            last_donation.setText(DataLoader.formatDate(DataLoader.profileInfo.last_donation));
        }
        //last_donation.setText(donation);
        phone.setText(DataLoader.profileInfo.phone);
        division.setText(DataLoader.profileInfo.division);
        district.setText(DataLoader.profileInfo.district);
        thana.setText(DataLoader.profileInfo.upazila);

        blood_group.setText(DataLoader.profileInfo.blood_group);

        if(!DataLoader.profileInfo.post_code.equals("na")){
            postalCode.setText(DataLoader.profileInfo.post_code);
        }

        if(!DataLoader.profileInfo.address.equals("na")){
            currentAddress.setText(DataLoader.profileInfo.address);
        }


        if(!DataLoader.profileInfo.religion.equals("na")){
            religion.setText(DataLoader.profileInfo.religion);
        }
        if(!DataLoader.profileInfo.is_physically_disble.equals("na")){
            physically_disable.setText(DataLoader.profileInfo.is_physically_disble);

        }
        if(!DataLoader.profileInfo.nationality.equals("na")){
            nationality.setText(DataLoader.profileInfo.nationality);
        }
        if(!DataLoader.profileInfo.nid.equals("na")){
            nid.setText(DataLoader.profileInfo.nid);
        }

            rank.setText(DataLoader.profileInfo.rank);

        String profile;
        if(DataLoader.profileInfo.pro_visible.equals("0")) {
            profile = "No";
        }
        else {
            profile = "Yes";
        }
        pro_visible.setText(profile);
        donations_number.setText(DataLoader.profileInfo.donations_number);
    }


    public void setProfilePic() {
        String url = DataLoader.profileInfo.pic_path;

        Glide.with(this).load(url)
                .apply(RequestOptions.placeholderOf(R.drawable.userpic_default))
                .override(100,100)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(pro_pic);


        //Log.d(TAG, "setProfilePic: "+url);
        /*ImageRequest imageRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                Bitmap circularBitmap = ImageConverter.getRoundedCornerBitmap(response, 100);
                pro_pic.setImageBitmap(circularBitmap);
            }
        }, 100, 100, ImageView.ScaleType.CENTER_CROP, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ProfileDetails.this, error.toString(), Toast.LENGTH_LONG).show();
                Log.d(TAG, "onErrorResponse: "+error.toString());
            }
        });
        imageRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(this).addToRequest(imageRequest);*/
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
    public void onClick(View v) {
        v.startAnimation(DataLoader.buttonClick);
        int id = v.getId();
        switch (id) {
            case R.id.back:
                finish();
                startActivity(new Intent(this,MainActivity.class));
                break;
            case R.id.edit:
                //startActivity(new Intent(this,EditProfileDetails.class));
                dialog.show();
                DataLoader.context = this;
                DataLoader.profileInfo = null;
                DataLoader.getUserFromServer("EditProfileDetails");
                break;
        }
    }






}
