package com.ourdreamit.blooddonationproject.dialogs;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ourdreamit.blooddonationproject.utils.DataLoader;
import com.ourdreamit.blooddonationproject.utils.ImageConverter;
import com.ourdreamit.blooddonationproject.utils.MySingleton;
import com.ourdreamit.blooddonationproject.R;

/**
 * Created by anismizi on 7/4/17.
 */

public class AdminDoctorBasicInfo extends DialogFragment implements View.OnClickListener {
    ImageView pro_pic;
    TextView name, blood_group, pro_visible, last_donation, donations_number, birth_date, phone, email, address;
    Button call, back;
    DataLoader.UserInfo userInfo;
    public static String receiverToken;
    LinearLayout firstDetails, secondDetails;
    public static Boolean doctor, admin;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.donor_basicinfo_fragment, null);
        pro_pic = (ImageView) view.findViewById(R.id.pro_pic);

        name = (TextView) view.findViewById(R.id.name);
        blood_group = (TextView) view.findViewById(R.id.blood_group);
        pro_visible = (TextView) view.findViewById(R.id.pro_visible);
        last_donation = (TextView) view.findViewById(R.id.last_donation);
        donations_number = (TextView) view.findViewById(R.id.donations_number);
        birth_date = (TextView) view.findViewById(R.id.birth_date);
        phone = (TextView) view.findViewById(R.id.phone);
        email = (TextView) view.findViewById(R.id.email);
        address = (TextView) view.findViewById(R.id.address);

        firstDetails = (LinearLayout) view.findViewById(R.id.firstDetails);
        secondDetails = (LinearLayout) view.findViewById(R.id.secondDetails);

        call = (Button) view.findViewById(R.id.call);
        call.setText("Call");
        //call.setVisibility(View.GONE);
        back = (Button) view.findViewById(R.id.back);

        call.setOnClickListener(this);
        back.setOnClickListener(this);

        if (doctor) {
            userInfo = DataLoader.activeDoctorDetails.get(DataLoader.currentMarker);
        } else if (admin) {
            userInfo = DataLoader.adminDetails.get(DataLoader.currentMarker);
        }
        setUserDetails();

        return view;
    }

    @Override
    public void onClick(View v) {
        v.startAnimation(DataLoader.buttonClick);
        int id = v.getId();

        switch (id) {
            case R.id.call:
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + userInfo.phone));
                startActivity(intent);
                break;
            case R.id.back:
                dismiss();
                break;
        }
    }


    public void setUserDetails() {
        //pic_path,fname,lname,email,phone,birthDate,bloodgroup,location,latitude,longitude


        String url = userInfo.pic_path;

        Glide.with(getActivity()).load(url)
                .apply(RequestOptions.placeholderOf(R.drawable.userpic_default))
                .override(70,70)
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
                //Toast.makeText(getActivity(),error.toString(),Toast.LENGTH_LONG).show();
            }
        });
        imageRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(getActivity()).addToRequest(imageRequest);*/



        name.setText(userInfo.fname + " " + userInfo.lname);
        blood_group.setText(userInfo.blood_group);
        if (userInfo.pro_visible.equals("1")) {
            pro_visible.setText("Available");
            last_donation.setText(userInfo.last_donation);
            donations_number.setText(userInfo.donations_number);
            birth_date.setText(userInfo.birth_date);
            phone.setText(userInfo.phone);
            email.setText(userInfo.email);
            address.setText(userInfo.address);
        } else {
            pro_visible.setText("Not Available");
            pro_visible.setTextColor(Color.RED);
            //firstDetails.setVisibility(View.GONE);
            //secondDetails.setVisibility(View.GONE);
            call.setText("Request");
        }


    }
}
