package com.ourdreamit.blooddonationproject.dialogs;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.ourdreamit.blooddonationproject.fcm.FcmNotificationBuilder;

/**
 * Created by anismizi on 7/4/17.
 */

public class DoctorBasicInfo extends DialogFragment implements View.OnClickListener{
    ImageView profile_photo;
    TextView name,designation,hospital,speacilist,phone,email,gender,doctor_detail,chamber_address;
    AppCompatButton call,back;
    DataLoader.DoctorInfo doctorInfo;
    public static String receiverToken;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.doctor_basicinfo_fragment,null);
        profile_photo = (ImageView) view.findViewById(R.id.profile_photo);

        name = (TextView) view.findViewById(R.id.name);
        designation = (TextView) view.findViewById(R.id.designation);
        hospital = (TextView) view.findViewById(R.id.hospital);
        speacilist = (TextView) view.findViewById(R.id.speacilist);
        phone = (TextView) view.findViewById(R.id.phone);
        email = (TextView) view.findViewById(R.id.email);
        gender = (TextView) view.findViewById(R.id.gender);
        doctor_detail = (TextView) view.findViewById(R.id.doctor_detail);
        chamber_address = (TextView) view.findViewById(R.id.chamber_address);


        call = (AppCompatButton) view.findViewById(R.id.call);
        back = (AppCompatButton) view.findViewById(R.id.back);

        call.setOnClickListener(this);
        back.setOnClickListener(this);

        doctorInfo = DataLoader.doctorDetails.get(DataLoader.currentMarker);
        setUserDetails();

        return view;
    }

    @Override
    public void onClick(View v) {
        v.startAnimation(DataLoader.buttonClick);
        int id = v.getId();

        switch (id){
            case R.id.call:
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + doctorInfo.phone));
                startActivity(intent);
                break;
            case R.id.back:
                dismiss();
                break;
        }
    }



    public static void sendRequest(){

        String username = DataLoader.profileInfo.fname+" "+DataLoader.profileInfo.lname;
        String message = username+" wants to get your profile details";
        String uid = DataLoader.profileInfo.fcm_uid;
        String firebaseToken =  DataLoader.profileInfo.fcm_token;
        String receiverFirebaseToken = receiverToken;

        FcmNotificationBuilder.initialize()
                .title("Profile Details")
                .message(message)
                .username(username)
                .uid(uid)
                .firebaseToken(firebaseToken)
                .receiverFirebaseToken(receiverFirebaseToken)
                .send();

    }

    public void setUserDetails(){
        //pic_path,fname,lname,email,phone,birthDate,bloodgroup,location,latitude,longitude


        String url = doctorInfo.profile_photo;

        Glide.with(getActivity()).load(url)
                .apply(RequestOptions.placeholderOf(R.drawable.userpic_default))
                .override(70,70)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(profile_photo);

        /*ImageRequest imageRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                Bitmap circularBitmap = ImageConverter.getRoundedCornerBitmap(response, 100);
                profile_photo.setImageBitmap(circularBitmap);
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


        name.setText(doctorInfo.name);
        designation.setText(doctorInfo.designation);
        hospital.setText(doctorInfo.hospital);
        //if(userInfo.pro_visible.equals("1")){
            speacilist.setText(doctorInfo.speacilist);
        phone.setText(doctorInfo.phone);
        email.setText(doctorInfo.email);
            gender.setText(doctorInfo.gender);
            doctor_detail.setText(doctorInfo.doctor_detail);

            chamber_address.setText(doctorInfo.chamber_address);



    }
}
