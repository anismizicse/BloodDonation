package com.ourdreamit.blooddonationproject.ui.adapters;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
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
import com.ourdreamit.blooddonationproject.utils.DataLoader.BloodRequests;
import com.ourdreamit.blooddonationproject.utils.ImageConverter;
import com.ourdreamit.blooddonationproject.utils.MySingleton;
import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.ui.activities.BloodRequestProfileDetails;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by anismizi on 7/7/17.
 */

public class BloodRequestsListAdapter extends RecyclerView.Adapter<BloodRequestsListAdapter.DoctorsViewHolder>  {
    private LayoutInflater inflater;
    private List<BloodRequests> data = Collections.emptyList();
    private Context context;
    Bitmap circularBitmap;
    public FragmentManager manager;
    static String receiverToken;

    int min = 1;
    int max = 5;
    Random rand = new Random();
    int code;

    public BloodRequestsListAdapter(Context ctx, List<BloodRequests> list, FragmentManager manager){
        context = ctx;
        this.manager = manager;
        inflater = LayoutInflater.from(context);
        data = list;
    }


    @Override
    public DoctorsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.bloodrequests_row, parent, false);
        DoctorsViewHolder holder = new DoctorsViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(DoctorsViewHolder holder, int position) {
        BloodRequests currentData = data.get(position);
        //holder.pro_pic.setImageBitmap(setProPic(currentData.pic_path));
        //setProPic(currentData.profile_photo,holder.pro_pic);
        holder.request_to.setText(currentData.request_to);
        //holder.phone.setText(currentData.phone);
        holder.request_blood.setText(currentData.request_blood);
        holder.acceptance.setText("accepted");
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
        TextView request_to, request_blood, acceptance;
        AppCompatButton details;


        public DoctorsViewHolder(View itemView) {
            super(itemView);

            CardView request_details = (CardView) itemView.findViewById(R.id.request_details);
            rand = new Random();
            code = rand.nextInt(max - min + 1) + min;

            switch (code){
                case 1:
                    request_details.setBackgroundColor(context.getResources().getColor(R.color.one));
                    break;
                case 2:
                    request_details.setBackgroundColor(context.getResources().getColor(R.color.six));
                    break;
                case 3:
                    request_details.setBackgroundColor(context.getResources().getColor(R.color.three));
                    break;
                case 4:
                    request_details.setBackgroundColor(context.getResources().getColor(R.color.four));
                    break;
                case 5:
                    request_details.setBackgroundColor(context.getResources().getColor(R.color.five));
                    break;
            }

            request_to = (TextView) itemView.findViewById(R.id.request_to);
            request_blood = (TextView) itemView.findViewById(R.id.request_blood);
            //Log.d("Callproblem","before "+phone.getText().toString());
            acceptance = (TextView) itemView.findViewById(R.id.acceptance);
            details = (AppCompatButton) itemView.findViewById(R.id.details);
            details.setOnClickListener(this);
            //itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            v.startAnimation(DataLoader.buttonClick);
            int position = this.getAdapterPosition();
            int id = v.getId();
            switch (id){
                case R.id.details:
                    BloodRequestProfileDetails.request_to = DataLoader.bloodRequests.get(position).request_to;
                    context.startActivity(new Intent(context,BloodRequestProfileDetails.class));
                    break;
            }

        }



    }




}
