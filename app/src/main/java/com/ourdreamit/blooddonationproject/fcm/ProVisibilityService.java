package com.ourdreamit.blooddonationproject.fcm;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.utils.DataLoader;
import com.ourdreamit.blooddonationproject.utils.MySingleton;
import com.ourdreamit.blooddonationproject.utils.SharedPrefUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by anismizi on 5/11/17.
 */

public class ProVisibilityService extends Service {



    @Override
    public void onCreate() {

    }

    @Override
    public int onStartCommand(Intent intent,  int flags, int startId) {
        //DataLoader.context = this;
        checkProVisibility();

        return super.onStartCommand(intent, flags, startId);
    }



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {

        //Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }

    public void checkProVisibility() {
        Calendar cal = Calendar.getInstance();
        int yy = cal.get(Calendar.YEAR);
        int mm = 1 + cal.get(Calendar.MONTH);
        int dd = cal.get(Calendar.DAY_OF_MONTH);
        final String dateInString = dd + "-" + mm + "-" + yy;
        String procheckDate = new SharedPrefUtil(ProVisibilityService.this).getString(DataLoader.PROCHECKDATE, "na");
        //Log.d("ProVisibility",procheckDate+" "+dateInString);
        if(!procheckDate.equals(dateInString)) {

            String app_server_url = getString(R.string.server_url) + "update_pro_visibility.php";
            //Toast.makeText(AllStatic.ctx, "inside updateDatabase", Toast.LENGTH_LONG).show();

            try {

                StringRequest stringRequest = new StringRequest(Request.Method.POST, app_server_url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if(response.equals("updated")) {
                                    //Toast.makeText(ProVisibilityService.this, response, Toast.LENGTH_LONG).show();
                                }
                                new SharedPrefUtil(ProVisibilityService.this).saveString(DataLoader.PROCHECKDATE, dateInString);

                                if (!response.equals("na")) {
                                    try {
                                        JSONArray jsonArray = new JSONArray(response);
                                        JSONObject mJsonObject;

                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            mJsonObject = jsonArray.getJSONObject(i);
                                            String password = mJsonObject.getString("password");
                                            String fcm_email = mJsonObject.getString("fcm_email");
                                            String fcm_uid = mJsonObject.getString("fcm_uid");
                                            String email = mJsonObject.getString("email");
                                            String fcm_token = mJsonObject.getString("fcm_token");

                                            String local_Token = new SharedPrefUtil(ProVisibilityService.this).getString(DataLoader.FCM_TOKEN, null);
                                            //Toast.makeText(CurrentLocation.this,local_Token+"\n "+fcm_token,Toast.LENGTH_LONG).show();
                                            if(local_Token != null && !fcm_token.equals(local_Token)){

                                                DataLoader.context = ProVisibilityService.this;
                                                DataLoader.removeLocalVars();
                                            }

                                        }

                                    } catch (JSONException e) {
                                        //e.printStackTrace();
                                    }

                                }

//                                DataLoader.TalkToProfileDetails talk;
//                                talk = (ProVisibilityService) context;
//                                talk.setUserDetails();
                                stopService();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //Log.d("updateLat", error.toString());
                                //new SharedPrefUtil(context).saveString(DataLoader.PROCHECKDATE, dateInString);
                                stopService();
//                                DataLoader.TalkToProfileDetails talk;
//                                talk = (ProVisibilityService) context;
//                                talk.setUserDetails();
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("phone", new SharedPrefUtil(ProVisibilityService.this).getString(DataLoader.PHONE_NUMBER, "na"));
                        params.put("autopro_visible", dateInString);
                        return params;
                    }
                };
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                MySingleton.getmInstance(ProVisibilityService.this).addToRequest(stringRequest);
            } catch (Exception e) {
                //Toast.makeText(context, "" + e, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void stopService(){
        this.stopSelf();
    }

//    @Override
//    public void setUserDetails() {
//        this.stopSelf();
//    }
}
