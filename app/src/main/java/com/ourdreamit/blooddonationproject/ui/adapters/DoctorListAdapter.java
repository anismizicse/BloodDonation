package com.ourdreamit.blooddonationproject.ui.adapters;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ourdreamit.blooddonationproject.utils.ImageConverter;
import com.ourdreamit.blooddonationproject.utils.MySingleton;
import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.utils.DataLoader;
import com.ourdreamit.blooddonationproject.utils.DataLoader.DoctorInfo;
import com.ourdreamit.blooddonationproject.dialogs.DoctorBasicInfo;
import com.ourdreamit.blooddonationproject.fcm.FcmNotificationBuilder;

/**
 * Created by anismizi on 7/7/17.
 */

public class DoctorListAdapter extends RecyclerView.Adapter<DoctorListAdapter.DoctorsViewHolder>  {
    private LayoutInflater inflater;
    private List<DoctorInfo> data = Collections.emptyList();
    private Context context;
    Bitmap circularBitmap;
    public FragmentManager manager;
    static String receiverToken;

    int min = 1;
    int max = 5;
    Random rand = new Random();
    int code;

    public DoctorListAdapter(Context ctx,List<DoctorInfo> list,FragmentManager manager){
        context = ctx;
        this.manager = manager;
        inflater = LayoutInflater.from(context);
        data = list;
    }


    @Override
    public DoctorsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.doctorlist_row, parent, false);
        DoctorsViewHolder holder = new DoctorsViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(DoctorsViewHolder holder, int position) {
        DoctorInfo currentData = data.get(position);
        //holder.pro_pic.setImageBitmap(setProPic(currentData.pic_path));
        setProPic(currentData.profile_photo,holder.pro_pic);
        holder.name.setText(currentData.name);
        //holder.phone.setText(currentData.phone);
        holder.phone.setText(currentData.phone);
        holder.address.setText(currentData.designation);
    }

    private void setProPic(String pic_path, final ImageView im) {

        String url = pic_path;
        Glide.with(context).load(url)
                .apply(RequestOptions.placeholderOf(R.drawable.userpic_default))
                .override(70,70)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(im);
        //Log.d("Callproblem","url "+url);
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

    public class DoctorsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;
        TextView name, phone, address;
        ImageView pro_pic;
        AppCompatButton details,call;


        public DoctorsViewHolder(View itemView) {
            super(itemView);

            CardView doctorView = (CardView) itemView.findViewById(R.id.doctorView);
            rand = new Random();
            code = rand.nextInt(max - min + 1) + min;

            switch (code){
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
            phone = (TextView) itemView.findViewById(R.id.phone);
            //Log.d("Callproblem","before "+phone.getText().toString());
            address = (TextView) itemView.findViewById(R.id.address);

            details = (AppCompatButton) itemView.findViewById(R.id.details);
            call = (AppCompatButton) itemView.findViewById(R.id.call);

            details.setOnClickListener(this);
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

                    t = (TextView) root.findViewById(R.id.phone);
                    x = t.getText().toString();

                    //Log.d("Callproblem","before permission");

                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + x));
                    context.startActivity(intent);



                    break;
                case R.id.details:
                    DataLoader.currentMarker = this.getAdapterPosition();
                    DoctorBasicInfo dialog = new DoctorBasicInfo();
                    dialog.show(manager,"marker"+this.getAdapterPosition());
                    break;
            }
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

}
