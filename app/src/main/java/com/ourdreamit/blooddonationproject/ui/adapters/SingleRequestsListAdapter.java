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
import android.widget.TextView;

import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.utils.DataLoader;
import com.ourdreamit.blooddonationproject.utils.DataLoader.BloodRequests;
import com.ourdreamit.blooddonationproject.ui.activities.AllBloodRequests;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

/**
 * Created by anismizi on 7/7/17.
 */

public class SingleRequestsListAdapter extends RecyclerView.Adapter<SingleRequestsListAdapter.DoctorsViewHolder>  {
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

    public SingleRequestsListAdapter(Context ctx, List<BloodRequests> list, FragmentManager manager){
        context = ctx;
        this.manager = manager;
        inflater = LayoutInflater.from(context);
        data = list;
    }


    @Override
    public DoctorsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.singlerequests_row, parent, false);
        DoctorsViewHolder holder = new DoctorsViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(DoctorsViewHolder holder, int position) {
        BloodRequests currentData = data.get(position);

        holder.request_time.setText(getDateCurrentTimeZone(
                Long.parseLong(currentData.request_time)));

        holder.request_blood.setText(currentData.request_blood);
        holder.deletion_date.setText(getDateCurrentTimeZone(
                Long.parseLong(currentData.deletion_date)));
    }

    public  String getDateCurrentTimeZone(long timestamp) {
        try{
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getTimeZone("UTC+06:00");
            calendar.setTimeInMillis(timestamp * 1000);
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
            Date currenTimeZone = (Date) calendar.getTime();
            return sdf.format(currenTimeZone);
        }catch (Exception e) {
        }
        return "";
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public class DoctorsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;
        TextView request_time, request_blood,deletion_date;
        AppCompatButton details;



        public DoctorsViewHolder(View itemView) {
            super(itemView);

            CardView singleRequestView = (CardView) itemView.findViewById(R.id.singleRequestView);
            rand = new Random();
            code = rand.nextInt(max - min + 1) + min;

            switch (code){
                case 1:
                    singleRequestView.setBackgroundColor(context.getResources().getColor(R.color.one));
                    break;
                case 2:
                    singleRequestView.setBackgroundColor(context.getResources().getColor(R.color.six));
                    break;
                case 3:
                    singleRequestView.setBackgroundColor(context.getResources().getColor(R.color.three));
                    break;
                case 4:
                    singleRequestView.setBackgroundColor(context.getResources().getColor(R.color.four));
                    break;
                case 5:
                    singleRequestView.setBackgroundColor(context.getResources().getColor(R.color.five));
                    break;
            }

            request_time = (TextView) itemView.findViewById(R.id.request_time);
            request_blood = (TextView) itemView.findViewById(R.id.request_blood);
            deletion_date = (TextView) itemView.findViewById(R.id.deletion_date);
            //Log.d("Callproblem","before "+phone.getText().toString());
            details = (AppCompatButton) itemView.findViewById(R.id.details);
            details.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {
            //v.startAnimation(DataLoader.buttonClick);
            int id = v.getId();
            int position = this.getAdapterPosition();
            switch (id){
                case R.id.details:
                    AllBloodRequests.request_phone = DataLoader.bloodRequests.get(position).request_phone;
                    AllBloodRequests.request_to = DataLoader.bloodRequests.get(position).request_to;
                    AllBloodRequests.request_time = DataLoader.bloodRequests.get(position).request_time;
                    //Log.d("blood_requests",AllBloodRequests.request_phone+" "+AllBloodRequests.request_to+" "+AllBloodRequests.request_time);
                    context.startActivity(new Intent(context,AllBloodRequests.class));
                    break;
            }

        }
    }




}
