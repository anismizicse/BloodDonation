package com.ourdreamit.blooddonationproject.utils;

import android.content.Context;
import android.view.MenuItem;

import com.ourdreamit.blooddonationproject.R;

/**
 * Created by anismizi on 4/17/17.
 */

public class MenuOptionsSelected {

    public MenuItem createMenuActions(MenuItem item,Context ctx){
        int id = item.getItemId();
        switch (id){
            case R.id.action_settings:
                //ctx.startActivity(new Intent(ctx,UserSettings.class));
                break;
            case R.id.more_apps:
                new OutsideAppActivity(ctx).moreApps();
                break;
            case R.id.rate_us:
                new OutsideAppActivity(ctx).rateApp();
                break;
        }



        return item;
    }
}
