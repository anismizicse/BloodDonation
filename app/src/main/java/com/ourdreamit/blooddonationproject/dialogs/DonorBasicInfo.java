package com.ourdreamit.blooddonationproject.dialogs;

import android.app.DialogFragment;
import android.app.ProgressDialog;
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
import com.ourdreamit.blooddonationproject.utils.DataLoader;
import com.ourdreamit.blooddonationproject.utils.ImageConverter;
import com.ourdreamit.blooddonationproject.utils.MySingleton;
import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.fcm.FcmNotificationBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by anismizi on 7/4/17.
 */

public class DonorBasicInfo extends DialogFragment implements View.OnClickListener {
    ImageView pro_pic;
    TextView name, blood_group, pro_visible, last_donation, donations_number, birth_date, phone, email, address;
    Button call, back;
    DataLoader.UserInfo userInfo;
    public static String receiverToken;
    LinearLayout firstDetails, secondDetails;
    int called_today;
    public String today_date;
    ProgressDialog dialog;

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
        back = (Button) view.findViewById(R.id.back);

        call.setOnClickListener(this);
        back.setOnClickListener(this);

        userInfo = DataLoader.userDetails.get(DataLoader.currentMarker);

        dialog = new ProgressDialog(getActivity(),R.style.MyAlertDialogStyle);
        dialog.setMessage("Loading...");
        //dialog.setProgressStyle(R.style.progressBarStyle);
        dialog.setCancelable(true);
        //dialog.setInverseBackgroundForced(true);
        dialog.setCanceledOnTouchOutside(true);

        dialog.show();

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
                getActivity().startActivity(intent);

                /*final Calendar cal = Calendar.getInstance();
                int yy = cal.get(Calendar.YEAR);
                int mm = 1 + cal.get(Calendar.MONTH);
                int dd = cal.get(Calendar.DAY_OF_MONTH);

                today_date = dd + "-" + mm + "-" + yy;
                Boolean available = true;
                if(!userInfo.called_date.equals("na")){
                    if(!userInfo.called_date.equals(today_date)){
                        available = true;
                    }else {
                        available = false;
                    }
                }

                if(called_today == 0 && available && userInfo.pro_visible.equals("1") && Integer.parseInt(userInfo.called_today) == 0) {
                    called_today = 1;
                    Log.d("DonorBasicInfo","Call is enabled");
                    updateCallStatus();
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + userInfo.phone));
                    getActivity().startActivity(intent);
                } else if(called_today == 1){
                    ErrorMsg.msg = "Sorry You can't call this number today anymore.";
                    ErrorMsg callErr = new ErrorMsg();
                    callErr.show(getFragmentManager(),"callErr");
                }else if(userInfo.called_today.equals("1")){
                    ErrorMsg.msg = "Sorry this donor has been called by other users. You can't call this number today anymore.";
                    ErrorMsg callErr = new ErrorMsg();
                    callErr.show(getFragmentManager(),"callErr");
                } else if(userInfo.pro_visible.equals("0") && called_today == 1){
                    Log.d("DonorBasicInfo","Notification is enabled");
                    DataLoader.context = getActivity();
                    DataLoader.profileInfo = null;
                    DataLoader.getUserFromServer("DonorBasicInfo");
                }



                    DataLoader.context = getActivity();
                    DataLoader.profileInfo = null;
                    DataLoader.getUserFromServer("DonorBasicInfo");*/
                break;
            case R.id.back:
                dismiss();
                break;
        }
    }

    private void updateCallStatus() {
        String app_server_url = getString(R.string.server_url) + "update_call_status.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, app_server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.d("phoneNumber",DataLoader.getUserPhone()+" "+code+" "+response.toString());

                        if (response.equals("updated")) {
                            Toast.makeText(getActivity(), "updated", Toast.LENGTH_LONG).show();

                        } else if (response.equals("error")) {

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getActivity(),"Network Error",Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("phoneverify","true");
                params.put("phone", DataLoader.getUserPhone());
                params.put("called_date", today_date);
                params.put("called_today", "" + 1);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(getActivity()).addToRequest(stringRequest);
    }

    public static void sendRequest() {

        String username = DataLoader.profileInfo.fname + " " + DataLoader.profileInfo.lname;
        String message = username + " wants to get your profile details";
        String uid = DataLoader.profileInfo.fcm_uid;
        String firebaseToken = DataLoader.profileInfo.fcm_token;
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

    public void setUserDetails() {
        //pic_path,fname,lname,email,phone,birthDate,bloodgroup,location,latitude,longitude

        dialog.hide();
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
                Toast.makeText(getActivity(),"Profile Photo couldn't be loaded",Toast.LENGTH_LONG).show();
            }
        });
        imageRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(getActivity()).addToRequest(imageRequest);*/

//        Log.d("userDetail", userInfo.pic_path + "\n" +
//                userInfo.fname + "\n" +
//                userInfo.lname + "\n" +
//                userInfo.email + "\n" +
//                userInfo.phone + "\n" +
//                userInfo.birth_date + "\n" +
//                userInfo.blood_group + "\n" +
//                userInfo.address + "\n" +
//                userInfo.latitude + "\n" +
//                userInfo.longitude + "\n" +
//                userInfo.fcm_email + "\n" +
//                userInfo.fcm_uid + "\n" +
//                userInfo.fcm_token + "\n" +
//                userInfo.age + "\n" +
//                userInfo.pro_visible + "\n" +
//                userInfo.called_date + "\n" +
//                userInfo.called_today + "\n" +
//                userInfo.last_donation + "\n");

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
        receiverToken = userInfo.fcm_token;

        /*donor_blood.setText(userInfo.blood_group);
        userData.setText(
                userInfo.pic_path+"\n"+
                        userInfo.fname+"\n"+
                        userInfo.lname+"\n"+
                        userInfo.email+"\n"+
                        userInfo.phone+"\n"+
                        userInfo.birth_date+"\n"+
                        userInfo.blood_group+"\n"+
                        userInfo.address+"\n"+
                        userInfo.latitude+"\n"+
                        userInfo.longitude+"\n"+
                        userInfo.fcm_email+"\n"+
                        userInfo.fcm_uid+"\n"+
                        userInfo.fcm_token+"\n"+
                        userInfo.age+"\n"+
                        userInfo.pro_visible+"\n"+
                        userInfo.called_date+"\n"+
                        userInfo.called_today+"\n"+
                        userInfo.last_donation+"\n"

        );*/

    }
}
