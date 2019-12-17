package com.ourdreamit.blooddonationproject.ui.adapters;

import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
import com.ourdreamit.blooddonationproject.ui.activities.DonorsList;
import com.ourdreamit.blooddonationproject.utils.ImageConverter;
import com.ourdreamit.blooddonationproject.utils.MySingleton;
import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.utils.DataLoader;
import com.ourdreamit.blooddonationproject.utils.DataLoader.UserInfo;
import com.ourdreamit.blooddonationproject.dialogs.AllDialog;
import com.ourdreamit.blooddonationproject.dialogs.DonorBasicInfo;
import com.ourdreamit.blooddonationproject.fcm.FcmNotificationBuilder;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by anismizi on 7/7/17.
 */

public class DonorListAdapter extends RecyclerView.Adapter<DonorListAdapter.DonorsViewHolder> {
    private LayoutInflater inflater;
    private List<UserInfo> data = Collections.emptyList();
    public static Context context;
    Bitmap circularBitmap;
    public FragmentManager manager;
    static String receiverToken, receiverBlood, receiverPhone;

    int min = 1;
    int max = 5;
    Random rand = new Random();
    int code;

    public DonorListAdapter(Context ctx, List<UserInfo> list, FragmentManager manager) {
        context = ctx;
        this.manager = manager;
        inflater = LayoutInflater.from(context);
        data = list;
    }


    @Override
    public DonorsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.donorlist_row, parent, false);
        DonorsViewHolder holder = new DonorsViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(DonorsViewHolder holder, int position) {

        UserInfo currentData = data.get(position);
        setProPic(currentData.pic_path, holder.pro_pic);
        holder.name.setText(currentData.fname + " " + currentData.lname);

    }

    private void setProPic(String pic_path, final ImageView im) {

        String url = pic_path;

        Glide.with(context).load(url)
                .apply(RequestOptions.placeholderOf(R.drawable.userpic_default))
                .override(70,70)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(im);

        /*ImageRequest imageRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                circularBitmap = ImageConverter.getRoundedCornerBitmap(response, 100);
                im.setImageBitmap(circularBitmap);
                //Log.d("Callproblem","Properties "+circularBitmap.getHeight()+ " "+circularBitmap.getWidth());
            }
        }, 0, 0, ImageView.ScaleType.CENTER_CROP, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(context,error.toString(),Toast.LENGTH_LONG).show();
            }
        });
        imageRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(context).addToRequest(imageRequest);*/

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class DonorsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name;
        ImageView pro_pic;
        AppCompatButton call;


        public DonorsViewHolder(View itemView) {
            super(itemView);

            CardView doctorView = (CardView) itemView.findViewById(R.id.doctorView);
            rand = new Random();
            code = rand.nextInt(max - min + 1) + min;

            switch (code) {
                case 1:
                    doctorView.setBackgroundColor(context.getResources().getColor(R.color.one));
                    break;
                case 2:
                    doctorView.setBackgroundColor(context.getResources().getColor(R.color.six));
                    break;
                case 3:
                    doctorView.setBackgroundColor(context.getResources().getColor(R.color.three));
                    break;
                case 4:
                    doctorView.setBackgroundColor(context.getResources().getColor(R.color.four));
                    break;
                case 5:
                    doctorView.setBackgroundColor(context.getResources().getColor(R.color.five));
                    break;
            }

            pro_pic = (ImageView) itemView.findViewById(R.id.pro_pic);
            name = (TextView) itemView.findViewById(R.id.name);

            call = (AppCompatButton) itemView.findViewById(R.id.call);

            DataLoader.context = context;
            if (DataLoader.getUserType().equals("admin")) {
                call.setText("Details");
            }

            call.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            v.startAnimation(DataLoader.buttonClick);
            View root = v.getRootView();
            //Log.d("Callproblem","After "+((TextView)root.findViewById(R.id.phone)).getText().toString());
            TextView t;
            String x;
            switch (v.getId()) {
                case R.id.call:

                    if (DataLoader.getUserType().equals("admin")) {
                        DataLoader.currentMarker = this.getAdapterPosition();
                        DonorBasicInfo dialog = new DonorBasicInfo();
                        dialog.show(manager, "marker" + this.getAdapterPosition());
                    } else {
                        receiverToken = DataLoader.userDetails.get(this.getAdapterPosition()).fcm_token;
                        receiverBlood = DataLoader.userDetails.get(this.getAdapterPosition()).blood_group;
                        receiverPhone = DataLoader.userDetails.get(this.getAdapterPosition()).phone;

                        new AllDialog(context).showDialog("Blood Request", "This donor will be notified about your blood request. Do you want to continue?",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        DataLoader.context = context;
                                        DataLoader.profileInfo = null;
                                        DataLoader.getUserFromServer("DonorListAdapter");
                                    }
                                }, "Yes", "No");

                    }

                    break;
            }
        }

    }


    public static void sendRequest() {
        JSONObject blood_request = new JSONObject();
        try {
            blood_request.put("requester_phone", DataLoader.getUserPhone());
            blood_request.put("requested_blood", receiverBlood);
            blood_request.put("bags", "1");
            blood_request.put("hospital", DonorsList.hospital);
            if (!DataLoader.profileInfo.address.equals("na"))
                blood_request.put("address", DataLoader.profileInfo.address);
            else
                blood_request.put("address", DataLoader.profileInfo.division+", "+
                        DataLoader.profileInfo.district+", "+DataLoader.profileInfo.upazila);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        String username = DataLoader.profileInfo.fname + " " + DataLoader.profileInfo.lname;

        String message = blood_request.toString();
        String uid = DataLoader.profileInfo.fcm_uid;
        String firebaseToken = DataLoader.profileInfo.fcm_token;
        String receiverFirebaseToken = receiverToken;

        FcmNotificationBuilder.initialize()
                .title("Blood Request")
                .message(message)
                .username(username)
                .uid(uid)
                .firebaseToken(firebaseToken)
                .receiverFirebaseToken(receiverFirebaseToken)
                .send();


    }

    public static void updateBloodRequest() {

        String app_server_url = context.getString(R.string.server_url) + "insert_blood_requests.php";

        try {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, app_server_url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //Toast.makeText(context, response, Toast.LENGTH_LONG).show();
                            if (response.equals("success")) {
                                sendRequest();
                                Toast.makeText(context, "Your request has been sent successfully.", Toast.LENGTH_LONG).show();
                            } else if (response.equals("managed")) {
                                Toast.makeText(context, "Sorry you can't make request today.", Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //Log.d("insert_blood_requests",error.toString());
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("request_phone", DataLoader.getUserPhone());
                    params.put("request_blood", receiverBlood);
                    params.put("blood_bags", DonorsList.blood_bags);
                    params.put("request_hospital", DonorsList.hospital);
                    params.put("request_address", DataLoader.profileInfo.address);
                    params.put("request_to", receiverPhone);
                    params.put("request_time", getTime("request_time"));
                    params.put("deletion_date", getTime("deletion_date"));
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MySingleton.getmInstance(context).addToRequest(stringRequest);
        } catch (Exception e) {
            Toast.makeText(context, "" + e, Toast.LENGTH_LONG).show();
        }
    }

    public static String getTime(String type) {
        if (type.equals("request_time")) {
            return "" + (System.currentTimeMillis() / 1000L);
        } else if (type.equals("deletion_date")) {

            //Gets todays date(day,month,year)
            final Calendar cal = Calendar.getInstance();
            int yy = cal.get(Calendar.YEAR);
            int mm = 1 + cal.get(Calendar.MONTH);
            int dd = cal.get(Calendar.DAY_OF_MONTH);

            String dateInString = dd + "-" + mm + "-" + yy;
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

            Calendar c = Calendar.getInstance();
            try {
                c.setTime(sdf.parse(dateInString));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            c.add(Calendar.DATE, 3);
            return "" + (c.getTimeInMillis() / 1000L);
        }
//        sdf = new SimpleDateFormat("dd-MM-yyyy");
//        Date resultdate = new Date(c.getTimeInMillis());
//        dateInString = sdf.format(resultdate);

        return "";
    }

}
