package com.ourdreamit.blooddonationproject.dialogs;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import com.ourdreamit.blooddonationproject.R;

/**
 * Created by anismizi on 2/28/18.
 */

public class AllDialog {
    public static Boolean verifyPhone,verifyReset;
    Context context;

    public AllDialog(Context ctx){
        this.context = ctx;
    }

    public void showDialog(String title,String message,DialogInterface.OnClickListener clickAction,String positiveMsg, String NegativeMsg){
        AlertDialog.Builder builder =
                new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);

        if(clickAction != null)
        builder.setPositiveButton(positiveMsg, clickAction);

        if(NegativeMsg != null)
        builder.setNegativeButton(NegativeMsg, null);

        builder.show();
    }

    public void showListDialog(String title,String[] list,DialogInterface.OnClickListener clickAction){
        AlertDialog.Builder builder =
                new AlertDialog.Builder(context);
        builder.setTitle(title);

        if(clickAction != null)
            builder.setItems(list,clickAction);

        builder.show();
    }

    public ProgressDialog showProgressDialog(String title,String message,Boolean cancelDialog,Boolean cancelDialogOutside){
        ProgressDialog mProgressDialog = new ProgressDialog(context,R.style.MyAlertDialogStyle);
        mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(cancelDialog);
        mProgressDialog.setCanceledOnTouchOutside(cancelDialogOutside);
        mProgressDialog.setIndeterminate(true);
        return mProgressDialog;
    }
}
