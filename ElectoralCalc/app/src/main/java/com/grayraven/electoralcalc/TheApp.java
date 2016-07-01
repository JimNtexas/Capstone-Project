package com.grayraven.electoralcalc;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * This class ensures Firebase persistence is activated before any other Firebase calls
 */
public class TheApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // FB database will be retained if power is removed from device and/or network connections fail or are intermittent.
        // See https://firebase.google.com/docs/reference/android/com/google/firebase/database/FirebaseDatabase.html#public-methods
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}