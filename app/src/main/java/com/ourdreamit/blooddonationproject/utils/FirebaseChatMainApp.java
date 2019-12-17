package com.ourdreamit.blooddonationproject.utils;

import androidx.multidex.MultiDexApplication;

import com.google.android.libraries.places.api.Places;
import com.google.firebase.FirebaseApp;
import com.ourdreamit.blooddonationproject.R;


public class FirebaseChatMainApp extends MultiDexApplication {
    private static boolean sIsChatActivityOpen = false;

    public static boolean isChatActivityOpen() {
        return sIsChatActivityOpen;
    }

    public static void setChatActivityOpen(boolean isChatActivityOpen) {
        FirebaseChatMainApp.sIsChatActivityOpen = isChatActivityOpen;
    }

    @Override
    public void onCreate() {
        FirebaseApp.initializeApp(this);
        // Initialize Places.
        Places.initialize(this, getString(R.string.maps_places_api));
        super.onCreate();
    }
}
