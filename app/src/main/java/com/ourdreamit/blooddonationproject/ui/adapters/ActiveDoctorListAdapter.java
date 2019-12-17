package com.ourdreamit.blooddonationproject.ui.adapters;

import android.app.FragmentManager;
import android.app.ProgressDialog;
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
import com.ourdreamit.blooddonationproject.utils.DataLoader.UserInfo;
import com.ourdreamit.blooddonationproject.utils.ImageConverter;
import com.ourdreamit.blooddonationproject.utils.MySingleton;
import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.dialogs.AllDialog;
import com.ourdreamit.blooddonationproject.ui.activities.SplashActivity;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by anismizi on 7/7/17.
 */

public class ActiveDoctorListAdapter extends RecyclerView.Adapter<ActiveDoctorListAdapter.DoctorsViewHolder>  {
    private LayoutInflater inflater;
    private List<UserInfo> data = Collections.emptyList();
    private Context context;
    Bitmap circularBitmap;
    public FragmentManager manager;

    int min = 1;
    int max = 5;
    Random rand = new Random();
    int code;

    public static ProgressDialog progressDialog;
    public static Boolean loadDonorToDoctor = false;


    public ActiveDoctorListAdapter(Context ctx, List<UserInfo> list, FragmentManager manager){
        context = ctx;
        this.manager = manager;
        inflater = LayoutInflater.from(context);
        data = list;
    }


    @Override
    public DoctorsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.activedoctorlist_row, parent, false);
        DoctorsViewHolder holder = new DoctorsViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(DoctorsViewHolder holder, int position) {
        UserInfo currentData = data.get(position);
        //holder.pro_pic.setImageBitmap(setProPic(currentData.pic_path));
        setProPic(currentData.pic_path,holder.pro_pic);
        holder.name.setText(currentData.fname+" "+currentData.lname);
        //holder.phone.setText(currentData.phone);
        holder.phone.setText(currentData.phone);
        if(!currentData.address.equals("na")) {
            holder.address.setText(currentData.address);
        }else
            holder.address.setVisibility(View.GONE);
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
        AppCompatButton message;


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

            //call = (Button) itemView.findViewById(R.id.call);
            message = (AppCompatButton) itemView.findViewById(R.id.message);

            //call.setOnClickListener(this);
            message.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //v.startAnimation(DataLoader.buttonClick);
            //int id = Integer.parseInt(DataLoader.adminDetails.get(this.getAdapterPosition()).id);
            //Log.d("Id",""+id);
            loadDonorToDoctor = true;
            progressDialog = new AllDialog(context).showProgressDialog("",
                    "Connecting to doctor...", true, true);
            progressDialog.setInverseBackgroundForced(true);
            progressDialog.show();
            switch (v.getId()) {

                case R.id.message:
                    DataLoader.tokenChanged = false;
                    //Log.d("DoctorPanel","ActiveDoctorListAdapterMessage");
                    DataLoader.currentMarker = this.getAdapterPosition();
                    DataLoader.donorToDoctorChat = true;
                    DataLoader.doctorToDonorChat = false;
                    DataLoader.doctorPhone = DataLoader.activeDoctorDetails.get(this.getAdapterPosition()).phone;
                    DataLoader.doctorPass = DataLoader.activeDoctorDetails.get(this.getAdapterPosition()).password;
                    DataLoader.profileInfo = null;
                    DataLoader.getUserFromServer("ActiveDoctorListAdapter");
                    context.startActivity(new Intent(context, SplashActivity.class));


                    break;
            }
        }

    }

    public void deleteDoctorChat(final String donor_phone,final String doctor_phone){
        //Toast.makeText(context, "delete "+id, Toast.LENGTH_LONG).show();
        //Log.d("deleteDoctorMsg",donor_phone+" "+doctor_phone);
        String app_server_url =  context.getString(R.string.server_url)+"insert_delete_doctormsg.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, app_server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(context, response, Toast.LENGTH_LONG).show();
                        if (response.equals("success")) {
//                            data.remove(adapterPos);
//                            notifyItemRemoved(adapterPos);
//                            notifyItemRangeChanged(adapterPos, data.size());
                            Toast.makeText(context,"Doctor chat has been emptied.",Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(context,"Couldn't be deleted",Toast.LENGTH_LONG).show();
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
                params.put("request_from", "patient");
                params.put("request_type", "update");
                params.put("id", "na");
                params.put("donor_phone", donor_phone);
                params.put("doctor_phone", doctor_phone);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(context).addToRequest(stringRequest);


    }




}
