package com.ourdreamit.blooddonationproject.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ourdreamit.blooddonationproject.utils.DataLoader;
import com.ourdreamit.blooddonationproject.utils.FirebaseChatMainApp;
import com.ourdreamit.blooddonationproject.ui.activities.FirebaseProfileDetails;
import com.ourdreamit.blooddonationproject.ui.activities.ProfileRequestAction;
import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.events.PushNotificationEvent;
import com.ourdreamit.blooddonationproject.ui.activities.ChatActivity;
import com.ourdreamit.blooddonationproject.ui.activities.SplashActivity;
import com.ourdreamit.blooddonationproject.utils.Constants;
import com.ourdreamit.blooddonationproject.utils.MySingleton;
import com.ourdreamit.blooddonationproject.utils.SharedPrefUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    String title = "";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        //Log.d(TAG, "onNewToken: "+token);
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(final String token) {
        new SharedPrefUtil(getApplicationContext()).saveString(Constants.ARG_FIREBASE_TOKEN, token);
        updateToken(token);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child(Constants.ARG_USERS)
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(Constants.ARG_FIREBASE_TOKEN)
                    .setValue(token);
        }
    }

    private void updateToken(final String token) {

        new SharedPrefUtil(this).saveString(DataLoader.FCM_TOKEN, token);

        //DataLoader.context = this;
        String app_server_url = getString(R.string.server_url)+"update_token.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, app_server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(MyFirebaseInstanceIDService.this, response, Toast.LENGTH_LONG).show();

                        if (response.equals("updated")) {
                            //startActivity(new Intent(MyFirebaseInstanceIDService.this, MainActivity.class));
//                            Boolean success = updateUser_Token(DataLoader.getUserPhone(),token);
//                            if(success){
                            //Toast.makeText(MyFirebaseInstanceIDService.this,"Updated Token",Toast.LENGTH_LONG).show();
                            //}

                        }else {

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("phoneverify","true");
                params.put("fcm_token", "" + token);
                params.put("phone", new SharedPrefUtil(getApplicationContext()).getString(DataLoader.PHONE_NUMBER, "na"));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(MyFirebaseMessagingService.this).addToRequest(stringRequest);
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        //Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            //Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            title = remoteMessage.getData().get("title");
            String message = remoteMessage.getData().get("text");
            String username = remoteMessage.getData().get("username");
            String uid = remoteMessage.getData().get("uid");
            String fcmToken = remoteMessage.getData().get("fcm_token");
            if (title.equals("Blood Request")) {
                sendNotification(title,
                        message,
                        username,
                        uid,
                        fcmToken);
            } else if (title.equals("Call Request Accepted")) {
                sendNotification(title,
                        message,
                        username,
                        uid,
                        fcmToken);
            }
//            else if (title.equals("Patient Message")) {
//                sendNotification(title,
//                        message,
//                        username,
//                        uid,
//                        fcmToken);
//            }
            else {

                // Don't show notification if chat activity is open.
                if (!FirebaseChatMainApp.isChatActivityOpen()) {
                    sendNotification(title,
                            message,
                            username,
                            uid,
                            fcmToken);
                } else if (FirebaseChatMainApp.isChatActivityOpen()) {
                    String userType = new SharedPrefUtil(this).getString(DataLoader.USERTYPE, "na");
                    if (userType.equals("admin") && !userType.equals("na")) {

                        if (DataLoader.isAdminChatting && DataLoader.donorChatProfile != null) {

                            if (!DataLoader.donorChatProfile.fcm_uid.equals(uid)) {
                                sendNotification(title,
                                        message,
                                        username,
                                        uid,
                                        fcmToken);
                            }

                        }else if (DataLoader.isDoctorChatting && DataLoader.donorChatProfile != null) {

                            if (!DataLoader.donorChatProfile.fcm_uid.equals(uid)) {
                                sendNotification(title,
                                        message,
                                        username,
                                        uid,
                                        fcmToken);
                            }

                        }

                    }
                } else {
                    EventBus.getDefault().post(new PushNotificationEvent(title,
                            message,
                            username,
                            uid,
                            fcmToken));
                }
            }
        }
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     */
    private void sendNotification(String title,
                                  String message,
                                  String receiver,
                                  String receiverUid,
                                  String firebaseToken) {


        if (title.equals("livechatAdmin")) {

            DataLoader.doctorCarePanel = false;
            DataLoader.doctorToDonorChat = false;
            DataLoader.donorToDoctorChat = false;
            title = receiver;

            DataLoader.profileInfo = new DataLoader.UserInfo();

        }else if (title.equals("livechatDonor")) {

            DataLoader.doctorCarePanel = false;
            DataLoader.doctorToDonorChat = false;
            DataLoader.donorToDoctorChat = false;
            title = receiver;

            DataLoader.profileInfo = new DataLoader.UserInfo();

        }else if (title.equals("doctorCareDoctor")) {

            DataLoader.doctorCarePanel = true;
            DataLoader.doctorToDonorChat = false;
            DataLoader.donorToDoctorChat = true;
            title = receiver;

            DataLoader.profileInfo = new DataLoader.UserInfo();

        }else if (title.equals("doctorCareDonor")) {

            DataLoader.doctorCarePanel = true;
            DataLoader.doctorToDonorChat = true;
            DataLoader.donorToDoctorChat = false;
            title = receiver;

            DataLoader.profileInfo = new DataLoader.UserInfo();
            Log.d("Finaltrace","Service Notification");

        }


        //Log.d("url", title + " " + message + " " + receiver + " " + receiverUid + " " + firebaseToken);
        Intent intent;
        if (title.equals("Blood Request")) {
            intent = new Intent(this, ProfileRequestAction.class);
            intent.putExtra(Constants.MESSAGE, message);
        }
        else if (title.equals("Call Request Accepted"))
            intent = new Intent(this, FirebaseProfileDetails.class);
//        else if (title.equals("Patient Message"))
//            intent = new Intent(this, DonorReply.class);
        else {
            String livechatlogin = new SharedPrefUtil(this).getString(DataLoader.LIVECHATLOGIN, "false");
            if (livechatlogin.equals("true")) {
                intent = new Intent(this, ChatActivity.class);
            }else {
                intent = new Intent(this, SplashActivity.class);
            }
        }

        int notificationId = new SharedPrefUtil(this).getInt(DataLoader.NOTIFICATIONID);
        notificationId += 1;
        new SharedPrefUtil(this).saveInt(DataLoader.NOTIFICATIONID, notificationId);

        intent.putExtra(Constants.ARG_RECEIVER, receiver);
        intent.putExtra(Constants.ARG_RECEIVER_UID, receiverUid);
        intent.putExtra(Constants.ARG_FIREBASE_TOKEN, firebaseToken);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        if (title.equals("Blood Request")) {
            message = "You got a blood request from LifeCycle. Your donation can save a life.";
        }

        String CHANNEL_ID = "my_channel_01";// The id of the channel.

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                /* Create or update. */
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                        "Notification Channel",
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
                notificationBuilder.setChannelId(CHANNEL_ID);
            }

            notificationManager.notify(notificationId, notificationBuilder.build());
        }catch (Exception e){
            //Log.d("NotifyErr",e.toString());
        }
    }
}