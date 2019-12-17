package com.ourdreamit.blooddonationproject.utils;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.Spanned;

import com.ourdreamit.blooddonationproject.R;

/**
 * Created by anismizi on 3/13/18.
 */

public class SetToolbar {

    public static Context context;


    public static Spanned setTitle(){
        return Html.fromHtml("<font color=\"#FFFFFF\">" + context.getString(R.string.app_name) + "</font>");
    }

    public static int setBgColor(){
        return Color.parseColor("#9C27B0");
    }
}
