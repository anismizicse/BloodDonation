package com.ourdreamit.blooddonationproject.ui.adapters;

import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.ourdreamit.blooddonationproject.ui.activities.Login;
import com.ourdreamit.blooddonationproject.utils.DataLoader;
import com.ourdreamit.blooddonationproject.utils.DataLoader.UserInfo;
import com.ourdreamit.blooddonationproject.utils.ImageConverter;
import com.ourdreamit.blooddonationproject.utils.MySingleton;
import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.dialogs.AdminDoctorBasicInfo;
import com.ourdreamit.blooddonationproject.dialogs.AllDialog;

/**
 * Created by anismizi on 7/7/17.
 */

public class AdminsListAdapter extends RecyclerView.Adapter<AdminsListAdapter.DoctorsViewHolder>  {
    private LayoutInflater inflater;
    private List<UserInfo> data = Collections.emptyList();
    private Context context;
    Bitmap circularBitmap;
    public FragmentManager manager;

    int min = 1;
    int max = 5;
    Random rand = new Random();
    int code;

    public AdminsListAdapter(Context ctx,List<UserInfo> list,FragmentManager manager){
        context = ctx;
        this.manager = manager;
        inflater = LayoutInflater.from(context);
        data = list;
    }

    public void addAdmin(UserInfo user){
        Toast.makeText(context,"Admin was added successfully",Toast.LENGTH_LONG).show();
        data.add(user);
        notifyItemInserted(data.size() - 1);
    }


    @Override
    public DoctorsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.doctorlist_row, parent, false);
        DoctorsViewHolder holder = new DoctorsViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(DoctorsViewHolder holder, int position) {
        UserInfo currentData = data.get(position);
        //holder.pro_pic.setImageBitmap(setProPic(currentData.pic_path));
        setProPic(currentData.pic_path, holder.pro_pic);
        holder.name.setText(currentData.fname + " " + currentData.lname);
        //holder.phone.setText(currentData.phone);
        holder.phone.setText(currentData.phone);
        if (!currentData.address.equals("na"))
            holder.address.setText(currentData.address);
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
        Button details,call;


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

            details = (Button) itemView.findViewById(R.id.details);
            call = (Button) itemView.findViewById(R.id.call);
            call.setText("Remove");

            details.setOnClickListener(this);
            call.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            final int position = this.getAdapterPosition();
            switch (v.getId()) {
                case R.id.call:
                    v.startAnimation(DataLoader.buttonClick);
                    new AllDialog(context).showDialog("Remove Admin", "Are you sure to remove this Admin?", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            removeAdmin(position);
                        }
                    },"Yes","No");

                    break;
                case R.id.details:
                    AdminDoctorBasicInfo.doctor = false;
                    AdminDoctorBasicInfo.admin = true;
                    DataLoader.currentMarker = this.getAdapterPosition();
                    AdminDoctorBasicInfo dialog = new AdminDoctorBasicInfo();
                    dialog.show(manager,"marker"+this.getAdapterPosition());
                    break;
            }
        }

    }

    private void removeAdmin(final int position) {

        final String tempPhoneNumber = DataLoader.adminDetails.get(position).phone;

        String url = context.getString(R.string.server_url)+"get_admin_settings.php";
        //Log.d("url",url);
        DataLoader.context = context;
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("admin_removed")){
                    data.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, data.size());
                    Toast.makeText(context,"Admin was removed successfully.",Toast.LENGTH_LONG).show();

                    DataLoader.context = context;
                    if(DataLoader.getUserPhone().equals(tempPhoneNumber)){

                        DataLoader.removeLocalVars();

                        Intent intent = new Intent(context, Login.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }

                }else{
                    Toast.makeText(context,"Admin was not removed.",Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,"Couldn't read url",Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("request_type", "admin_remove");
                params.put("update_hotline", "na");
                params.put("update_ambulance", "na");
                params.put("admin_phone", DataLoader.adminDetails.get(position).phone);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(context).addToRequest(stringRequest);
    }



}
