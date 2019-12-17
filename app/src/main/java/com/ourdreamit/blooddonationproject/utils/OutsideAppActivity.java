package com.ourdreamit.blooddonationproject.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by anismizi on 4/18/17.
 */

public class OutsideAppActivity {
    Context context;
    public OutsideAppActivity(Context ctx){
        context = ctx;
    }

    public void rateApp(){
        Intent playstore_link = new Intent();
        playstore_link.setAction(Intent.ACTION_VIEW);
        playstore_link.addCategory(Intent.CATEGORY_BROWSABLE);
        playstore_link.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.ourdreamit.banglahasirjokes"));
        context.startActivity(playstore_link);
    }


    public void moreApps(){
        Intent our_apps = new Intent();
        our_apps.setAction(Intent.ACTION_VIEW);
        our_apps.addCategory(Intent.CATEGORY_BROWSABLE);
        our_apps.setData(Uri.parse("https://play.google.com/store/apps/developer?id=Dream%20IT"));
        context.startActivity(our_apps);
    }


}
