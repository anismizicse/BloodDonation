package com.ourdreamit.blooddonationproject.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.ui.activities.Developer;
import com.ourdreamit.blooddonationproject.ui.activities.MainActivity;
import com.ourdreamit.blooddonationproject.utils.DataLoader;
import com.ourdreamit.blooddonationproject.utils.Information;

import java.util.Collections;
import java.util.List;

/**
 * Created by Anis on 10/23/2016.
 */
public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.MyViewHolder> {
    private LayoutInflater inflater;
    private Context context;
    List<Information> data = Collections.emptyList();
    //Sqlite_Database sqlite_database;
    SQLiteDatabase db;
    Activity activity;

    public NavigationDrawerAdapter(Context context, List<Information> data) {
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.context = context;
        activity = (Activity) context;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_row, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(NavigationDrawerAdapter.MyViewHolder holder, final int position) {
        Information current = data.get(position);
        holder.title.setText(current.title);
        holder.icon.setImageResource(current.iconId);


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        ImageView icon;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.listTitle);
            icon = (ImageView) itemView.findViewById(R.id.listIcon);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            switch (position) {
                case 0:
                    DataLoader.mDrawerLayout.closeDrawers();
                    //AllStaticVar.fromSettingsPage = false;
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    activity.overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
                    break;
                case 1:
                    //context.startActivity(new Intent(context,Saving_Settings.class));
                    Intent our_apps = new Intent();
                    our_apps.setAction(Intent.ACTION_VIEW);
                    our_apps.addCategory(Intent.CATEGORY_BROWSABLE);
                    our_apps.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.ourdreamit.blooddonationproject"));
                    context.startActivity(our_apps);
                    DataLoader.mDrawerLayout.closeDrawers();
                    activity.overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
                    break;
                case 2:
                    //new OutsideAppActivity(context).moreApps();
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String msg = "Download this lifesaver app from https://play.google.com/store/apps/details?id=com.ourdreamit.blooddonationproject";
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "LifeCycle");
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, msg);
                    context.startActivity(Intent.createChooser(sharingIntent, "Share Via"));
                    DataLoader.mDrawerLayout.closeDrawers();
                    break;
                case 3:
                    //context.startActivity(new Intent(context,Saving_Settings.class));
                    Intent website = new Intent();
                    website.setAction(Intent.ACTION_VIEW);
                    website.addCategory(Intent.CATEGORY_BROWSABLE);
                    website.setData(Uri.parse("http://lifecyclebd.org/"));
                    context.startActivity(website);
                    DataLoader.mDrawerLayout.closeDrawers();
                    activity.overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
                    break;
                case 4:

                    context.startActivity(new Intent(context,Developer.class));

                    DataLoader.mDrawerLayout.closeDrawers();
                    break;
                case 5:

                    Intent i = new Intent(context, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("Exit me", true);
                    context.startActivity(i);
                    activity.overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
                    activity.finish();

                    DataLoader.mDrawerLayout.closeDrawers();
                    break;
            }
        }
    }
}
