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
import com.ourdreamit.blooddonationproject.utils.MySingleton;
import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.ui.activities.HospitalDetails;
import com.ourdreamit.blooddonationproject.utils.DataLoader;
import com.ourdreamit.blooddonationproject.utils.DataLoader.HospitalInfo;

/**
 * Created by anismizi on 7/7/17.
 */

public class HospitalListAdapter extends RecyclerView.Adapter<HospitalListAdapter.HospitalsViewHolder>  {
    private LayoutInflater inflater;
    private List<HospitalInfo> data = Collections.emptyList();
    private Context context;
    Bitmap circularBitmap;
    public FragmentManager manager;

    int min = 1;
    int max = 5;
    Random rand = new Random();
    int code;

    public HospitalListAdapter(Context ctx, List<HospitalInfo> list, FragmentManager manager){
        context = ctx;
        this.manager = manager;
        inflater = LayoutInflater.from(context);
        data = list;
    }


    @Override
    public HospitalsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.hospitallist_row, parent, false);
        HospitalsViewHolder holder = new HospitalsViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(HospitalsViewHolder holder, int position) {
        HospitalInfo currentData = data.get(position);
        //holder.pro_pic.setImageBitmap(setProPic(currentData.pic_path));
        setProPic(currentData.pic_path,holder.pro_pic);
        holder.name.setText(currentData.name);
        holder.address.setText(currentData.address);
    }

    private void setProPic(String pic_path, final ImageView im) {

        //String url = context.getString(R.string.server_url)+pic_path;
        String url = pic_path;
        Glide.with(context).load(url)
                .apply(RequestOptions.placeholderOf(R.drawable.hospital_logo))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(im);
        //Log.d("Callproblem","url "+url);
        /*ImageRequest imageRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                //circularBitmap = ImageConverter.getRoundedCornerBitmap(response, 100);
                im.setImageBitmap(response);
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

    public class HospitalsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;
        TextView name, address;
        ImageView pro_pic;
        AppCompatButton details;


        public HospitalsViewHolder(View itemView) {
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
            address = (TextView) itemView.findViewById(R.id.address);

            details = (AppCompatButton) itemView.findViewById(R.id.details);
            details.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            v.startAnimation(DataLoader.buttonClick);
            switch (v.getId()) {

                case R.id.details:
                    DataLoader.currentMarker = this.getAdapterPosition();
//                    HospitalDetails dialog = new HospitalDetails();
//                    dialog.show(manager,"marker"+this.getAdapterPosition());
                    context.startActivity(new Intent(context,HospitalDetails.class));
                    break;
            }
        }
    }
}
