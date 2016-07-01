package com.grayraven.electoralcalc;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.grayraven.electoralcalc.PoJos.HistoryDataReady;
import com.grayraven.electoralcalc.PoJos.Utilities;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class ElectionHistoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

    private static final String TAG = "ElectionHistory";
    private SimpleCursorAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ListView mListView;
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

        mListView.setOnItemClickListener(this);
        mListView.requestFocus();

        getContent();

    }

    @SuppressWarnings("unused")
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


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView txtYear = (TextView) view.findViewById(R.id.history_year);
        String year = (String) txtYear.getText();
        Log.d(TAG, "clicked: " + year);
        //display the corresponding wiki page if the network is avaiable
        if(Utilities.isNetworkAvailable(this)){
            String url = getString(R.string.wiki_url) + year;
            Uri uri = Uri.parse(url);
            Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(launchBrowser);

        } else {
            View v = findViewById(R.id.election_history_view);
            if (v != null)  Snackbar.make(v ,getString(R.string.no_network), Snackbar.LENGTH_LONG).show();
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}