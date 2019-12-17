package com.ourdreamit.blooddonationproject.dialogs;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ourdreamit.blooddonationproject.ui.activities.AdminSettings;
import com.ourdreamit.blooddonationproject.utils.DataLoader;
import com.ourdreamit.blooddonationproject.utils.MySingleton;
import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.utils.SharedPrefUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by anismizi on 6/20/17.
 */

public class ChangeHotline extends DialogFragment implements View.OnClickListener {

    Button cancel, update;
    TextView hotlineNumErr;
    EditText newHotline;
    public String phoneNumber, update_hotline, update_ambulance;
    public static boolean hotline, ambulance, add_admin, add_doctor;
    public static Context context;
    SettingsAction settingsAction;
    public static DataLoader.UserInfo profileInfo;

    public interface SettingsAction {
        void performAction();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.changehotline_dialog, null);
        cancel = (Button) view.findViewById(R.id.cancel);
        update = (Button) view.findViewById(R.id.update);

        settingsAction = (AdminSettings) context;

        newHotline = (EditText) view.findViewById(R.id.newHotline);

        cancel.setOnClickListener(this);
        update.setOnClickListener(this);

        hotlineNumErr = (TextView) view.findViewById(R.id.hotlineNumErr);
        //setCancelable(false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        return view;
    }


    @Override
    public void onClick(View v) {
        v.startAnimation(DataLoader.buttonClick);
        int id = v.getId();

        if (id == R.id.cancel) {
            dismiss();
        } else if (id == R.id.update) {
            phoneNumber = newHotline.getText().toString();
            if (newHotline.getText().toString().isEmpty()) {
                hotlineNumErr.setVisibility(View.VISIBLE);
            } else {
                hotlineNumErr.setVisibility(View.GONE);
                updateHotline();
            }
            //dismiss();
        }

    }

    private void updateHotline() {
        DataLoader.context = getActivity();
        if (hotline) {
            update_hotline = phoneNumber;
            update_ambulance = "na";
            updateHotlineNums();
        } else if (ambulance) {
            update_hotline = "na";
            update_ambulance = phoneNumber;
            updateHotlineNums();
        } else if (add_doctor || add_admin) {
            findDoctorAdmin();
        }


    }
    private void updateHotlineNums(){
        String url = getString(R.string.server_url) + "get_admin_settings.php";
        //Log.d("url",url);

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dismiss();
                if (hotline && response.equals("hotlineupdated")) {
                    AdminSettings.hotlinePhone.setText(update_hotline);
                    DataLoader.hotline = update_hotline;
                    new SharedPrefUtil(getActivity()).saveString(DataLoader.HOTLINE, update_hotline);
                    Toast.makeText(getActivity(), "Hotline Number was updated successfully", Toast.LENGTH_SHORT).show();
                } else if (ambulance && response.equals("ambulanceupdated")) {
                    AdminSettings.ambulancePhone.setText(update_ambulance);
                    DataLoader.ambulane = update_ambulance;
                    new SharedPrefUtil(getActivity()).saveString(DataLoader.AMBULANCE, update_ambulance);
                    Toast.makeText(getActivity(), "Ambulance Hotline was updated successfully", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getActivity(), "Was not updated.", Toast.LENGTH_SHORT).show();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Couldn't read url", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("request_type", "update");
                params.put("update_hotline", update_hotline);
                params.put("update_ambulance", update_ambulance);
                params.put("last_updated_by", DataLoader.getUserPhone());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(getActivity()).addToRequest(stringRequest);
    }

    private void findDoctorAdmin() {
        String app_server_url = context.getString(R.string.server_url) + "find_doctor_admin.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, app_server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("na"))
                        {
                            Toast.makeText(context,"Sorry no active user found with this phone number",Toast.LENGTH_LONG).show();
                            dismiss();

                        }else {


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


                                }

                            } catch (JSONException e) {
                                //Log.d("adminDoctor",phoneNumber+" "+add_admin+" "+add_doctor+" "+e.toString());
                            }
                            settingsAction.performAction();
                            dismiss();
//                            if(add_doctor){
//                                AdminSettings.addActiveDoctor();
//                                DataLoader.activeDoctorDetails.add(profileInfo);
//                                dismiss();
//                            }else if(add_admin) {
//                                AdminSettings.addAdmin();
//                                DataLoader.adminDetails.add(profileInfo);
//                                dismiss();
//                            }
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Network Error", Toast.LENGTH_LONG).show();
                        //Log.d("myStatus",error.toString());

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("phoneverify","true");
                //Log.d("adminDoctor",phoneNumber+" "+add_admin+" "+add_doctor);
                params.put("phone", phoneNumber);
                if (add_admin)
                    params.put("request_for", "admin");
                else if (add_doctor)
                    params.put("request_for", "doctor");
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(context).addToRequest(stringRequest);
    }
}
