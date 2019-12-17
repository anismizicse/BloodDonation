package com.ourdreamit.blooddonationproject.ui.adapters;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.ourdreamit.blooddonationproject.utils.ImageConverter;
import com.ourdreamit.blooddonationproject.utils.MySingleton;
import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.utils.DataLoader;
import com.ourdreamit.blooddonationproject.utils.DataLoader.AdminChatListInfo;
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

public class DoctorChatListAdapter extends RecyclerView.Adapter<DoctorChatListAdapter.DonorsViewHolder> {
    private LayoutInflater inflater;
    private List<AdminChatListInfo> data = Collections.emptyList();
    private Context context;
    Bitmap circularBitmap;
    public FragmentManager manager;
    //public static ProgressDialog dialog;
    int min = 1;
    int max = 5;
    Random rand = new Random();
    int code;

    public static ProgressDialog progressDialog;
    public static Boolean loadDoctorToDonor = false;


    public DoctorChatListAdapter(Context ctx, List<AdminChatListInfo> list, FragmentManager manager) {
        context = ctx;
        this.manager = manager;
        inflater = LayoutInflater.from(context);
        data = list;
    }


    @Override
    public DonorsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.adminchatlist_row, parent, false);
        DonorsViewHolder holder = new DonorsViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(DonorsViewHolder holder, int position) {
        AdminChatListInfo currentData = data.get(position);
        //holder.pro_pic.setImageBitmap(setProPic(currentData.pic_path));
        setProPic(currentData.pic_path, holder.pro_pic);
        holder.name.setText(currentData.name);
        //holder.phone.setText(currentData.phone);
        holder.phone.setText(currentData.donor_phone);
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

    public class DonorsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;
        TextView name, phone, address;
        ImageView pro_pic;
        AppCompatButton delete, chat;


        public DonorsViewHolder(View itemView) {
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

            delete = (AppCompatButton) itemView.findViewById(R.id.delete);
            chat = (AppCompatButton) itemView.findViewById(R.id.chat);

            delete.setOnClickListener(this);
            chat.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {


            try {

                loadDoctorToDonor = true;
                progressDialog = new AllDialog(context).showProgressDialog("",
                        "Connecting to patient...", true, true);


                int id = Integer.parseInt(DataLoader.chatListDetails.get(this.getAdapterPosition()).id);
                //Log.d("Id",""+id);
                switch (v.getId()) {
                    case R.id.delete:
                        final int donorid = id;
                        final int adapterPos = this.getAdapterPosition();


                        new AllDialog(context).showDialog("Delete Chat", "Are you sure to delele this chat history?", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteDonorFromList(donorid, adapterPos);
                                progressDialog.setMessage("Deleting Donor from List.");
                                progressDialog.show();
                            }
                        },"Yes","No");


                        break;
                    case R.id.chat:
                        DataLoader.tokenChanged = false;
                        progressDialog.setMessage("Connecting to patient...");
                        progressDialog.show();
                        DataLoader.chatProfilePhone = DataLoader.chatListDetails.get(this.getAdapterPosition()).donor_phone;
                        DataLoader.donorToDoctorChat = false;
                        DataLoader.doctorToDonorChat = true;
                        //Log.d("chatListDetails", DataLoader.chatListDetails.size() + " " + DataLoader.chatProfilePhone);
                        context.startActivity(new Intent(context, SplashActivity.class));
                        break;
                }
            } catch (Exception e) {

            }
        }

    }

    public void deleteDonorFromList(final int id, final int position) {
        //Toast.makeText(context, "delete "+id, Toast.LENGTH_LONG).show();
        String app_server_url = context.getString(R.string.server_url) + "delete_patientchat.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, app_server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        //Toast.makeText(context, response, Toast.LENGTH_LONG).show();
                        if (response.equals("deleted")) {
                            data.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, data.size());
                            Toast.makeText(context, "Donor deleted from chat list.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, "Couldn't be deleted", Toast.LENGTH_LONG).show();
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
                params.put("request_type", "delete");
                params.put("id", "" + id);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(context).addToRequest(stringRequest);


    }


}
