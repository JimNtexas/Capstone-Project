package com.grayraven.electoralcalc;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.grayraven.electoralcalc.PoJos.HistoryDataReady;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class ElectionHistoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "ElectionHistory";
       SimpleCursorAdapter mAdapter;
       ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_election_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mListView = (ListView) findViewById(R.id.history_list);

        mAdapter = new SimpleCursorAdapter(getBaseContext(),
                R.layout.history_list_item,
                null,
                new String[] { HistoryContentProvider.ELECTION_YEAR, HistoryContentProvider.ELECTION_TEXT},
                new int[] { R.id.history_year , R.id.history_text}, 0);

        mListView.setAdapter(mAdapter);
     //   getLoaderManager().initLoader(0, null, this);

        getContent();

    }

    @Subscribe
    public void onMessageEvent(HistoryDataReady event) {
        Log.d(TAG, "data ready");
        getLoaderManager().initLoader(0, null, this);
    }

    private void getContent() {
        Intent intent = new Intent(this, DbService.class);
        startService(intent);
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = HistoryContentProvider.CONTENT_URI;
        return new CursorLoader(this, uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
//http://www.concretepage.com/android/android-asynctaskloader-example-with-listview-and-baseadapter