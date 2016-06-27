package com.grayraven.electoralcalc;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.grayraven.electoralcalc.PoJos.HistoryDataReady;

import org.greenrobot.eventbus.EventBus;

/*
 * A service to access Summary text for a past presidential election
 */
@SuppressWarnings("unused")
public class DbService extends IntentService {

    private static final String TAG = "DbService";
    private Uri uri = HistoryContentProvider.CONTENT_URI;


    public static final String EXTRA_YEAR = "com.grayraven.extra.YEAR";
    public static final String EXTRA_TEXT = "com.grayraven.extra.TEXT";

    public DbService() {
        super("DbService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            downloadFirebaseHistory();
        }
    }

    private void downloadFirebaseHistory() {
        DatabaseReference fdb = FirebaseDatabase.getInstance().getReference();
        String path = getString(R.string.history_path);
        fdb.child(path).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, dataSnapshot.toString());
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            Log.d(TAG, "key: " + child.getKey() + child.getValue().toString());
                            writeData(child.getKey(), child.getValue().toString());
                        }
                        EventBus.getDefault().post(new HistoryDataReady());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "dbService read cancelled", databaseError.toException());
                    }
                });
    }


    /* ---   I may use this function in version 2 ---*/
// --Commented out by Inspection START (6/26/2016 11:54 PM):
//    /**
//     * Handle Read in the provided background thread
//     */
//    private void handleRead() {
//
//        Cursor c =  getContentResolver().query(uri, null, null, null, null);
//
//        if(c.moveToFirst()) {
//            do {
//                String year = c.getString(c.getColumnIndex(HistoryContentProvider.ELECTION_YEAR));
//                String text = c.getString(c.getColumnIndex(HistoryContentProvider.ELECTION_TEXT));
//            } while (c.moveToNext());
//        }
//        c.close();
//        // send data back using event if desired
//    }
// --Commented out by Inspection STOP (6/26/2016 11:54 PM)

    /**
     * Handle action Write in the provided background thread with the provided
     * parameters.
     */
    private void writeData(String year, String text) {
        ContentValues values = new ContentValues();
        values.put(HistoryContentProvider.ELECTION_YEAR, Integer.parseInt(year));
        values.put(HistoryContentProvider.ELECTION_TEXT, text);
        Log.d(TAG, "inserting " + year + " - " + text);
        Uri uri = getContentResolver().insert(
                HistoryContentProvider.CONTENT_URI, values);
    }
}
//https://en.wikipedia.org/wiki/United_States_presidential_election,_2000