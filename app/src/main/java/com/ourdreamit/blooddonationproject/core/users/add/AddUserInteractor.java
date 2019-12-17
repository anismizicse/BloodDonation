package com.ourdreamit.blooddonationproject.core.users.add;

import android.content.Context;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.models.User;
import com.ourdreamit.blooddonationproject.ui.fragments.RegisterFragment;
import com.ourdreamit.blooddonationproject.utils.Constants;
import com.ourdreamit.blooddonationproject.utils.SharedPrefUtil;


public class AddUserInteractor implements AddUserContract.Interactor {
    private AddUserContract.OnUserDatabaseListener mOnUserDatabaseListener;


    public AddUserInteractor(AddUserContract.OnUserDatabaseListener onUserDatabaseListener) {
        //Log.d("ChatTrace","AddUserInteractor AddUserInteractor");
        this.mOnUserDatabaseListener = onUserDatabaseListener;
    }

    @Override
    public void addUserToDatabase(final Context context, FirebaseUser firebaseUser) {
//        Log.d("ChatTrace","AddUserInteractor addUserToDatabase");
//        Log.d("userDetails","useremail: "+firebaseUser.getEmail()
//                +"useremail: "+firebaseUser.getEmail()
//                +"userID: "+firebaseUser.getUid()
//                +"Token: "+new SharedPrefUtil(context).getString(Constants.ARG_FIREBASE_TOKEN)
//        );
        RegisterFragment.fcm_email = firebaseUser.getEmail();
        RegisterFragment.fcm_uid = firebaseUser.getUid();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        User user = new User(firebaseUser.getUid(),
                firebaseUser.getEmail(),
                new SharedPrefUtil(context).getString(Constants.ARG_FIREBASE_TOKEN));
        database.child(Constants.ARG_USERS)
                .child(firebaseUser.getUid())
                .setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mOnUserDatabaseListener.onSuccess(context.getString(R.string.user_successfully_added));
                        } else {
                            mOnUserDatabaseListener.onFailure(context.getString(R.string.user_unable_to_add));
                        }
                    }
                });
    }


}
