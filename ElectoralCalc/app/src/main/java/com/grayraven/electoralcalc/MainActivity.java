package com.grayraven.electoralcalc;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.grayraven.electoralcalc.PoJos.Election;
import com.grayraven.electoralcalc.PoJos.RecycleViewClickMsg;
import com.grayraven.electoralcalc.PoJos.Utilities;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    public static final String SIGN_OUT = "signout";
    private static final String TAG = "Main";
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    ValueEventListener mListener;
    Gson mGson = new Gson();
    private FirebaseAuth.AuthStateListener mAuthListener;

    private RecyclerView mRecycler;
    private ElectionAdapter mAdapter;
    ArrayList<Election> mElections = new ArrayList<Election>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);


        // Setup recycler view
        //todo:  if time permits implement swipe to delete like this:  https://github.com/nemanja-kovacevic/recycler-view-swipe-to-delete
        mRecycler = (RecyclerView) findViewById(R.id.recycler_view);
        mRecycler.setHasFixedSize(true);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new ElectionAdapter(mElections);
        mRecycler.setAdapter(mAdapter);

        // Setup Firebase
        mAuth= FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if(mUser != null) {
            String displayName = mUser.getUid();
            if (displayName == null) {
                displayName = "none";
            }
            String email = mUser.getEmail();
            if (email == null) {
                email = "";
            }
        }

        //Firebase data changes
        mListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mElections.clear();
                Log.d(TAG, "Value event change cnt: " + dataSnapshot.getChildrenCount());
                String path = String.format(getString(R.string.election_path_format),mUser.getUid(), "");
                DataSnapshot elections = dataSnapshot.child(path);
                for(DataSnapshot  el: elections.getChildren()){
                    String json = el.getValue().toString();
                    Election election = mGson.fromJson(json, Election.class);
                    Log.d(TAG, "election: " + election.getTitle());
                    mElections.add(election);
                }
                Utilities.SortElectionByTitle(mElections);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "FB DatabaseError " + databaseError.getMessage()); // probably the user is not logged in
                showLoginScreen();

            }
        };

        FirebaseDatabase.getInstance().getReference().addValueEventListener(mListener);

    } // end onCreate

    // handle clicks from mRecycler
    @Subscribe
    public void onRecycleViewClickMsg(RecycleViewClickMsg msg){
        Log.d(TAG, "recycle click:" + msg.getAction());  //http://stackoverflow.com/questions/26076965/android-recyclerview-addition-removal-of-items
        switch(msg.getAction()){
            case RecycleViewClickMsg.DELETE_ELECTION:
                Log.d(TAG, "Delete " + mElections.get(msg.getPosition()).getTitle());
                deleteElection(msg.getPosition());
                break;
            case RecycleViewClickMsg.OPEN_ELECTION:
                Log.d(TAG, "Open " + mElections.get(msg.getPosition()).getTitle());
                loadElectionGrid(mElections.get(msg.getPosition()));
                break;
        }
    }


    private void handleDatabaseError() {
        Log.d(TAG, "handleDatabaseError");
    } //todo: handle this

    private void showLoginScreen() {
        mAuth.signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(getString(R.string.log_out));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        showLoginScreen();
        return true;
    }

    @OnClick(R.id.fab)
    protected void showElectionGrid() {
        String selection = "2016";
        final AlertDialog.Builder chooseYearDlg = new AlertDialog.Builder(this);
        chooseYearDlg.setTitle(R.string.choose_year_title);
        final String[] years = {"2000", "2004","2008","2012","2016"};
        chooseYearDlg.setSingleChoiceItems(years, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(), ElectionGrid.class);
                intent.putExtra("election_year", years[which]);
                intent.putExtra("election_json", "");
                startActivity(intent);
                dialog.dismiss();
            }
        });
        chooseYearDlg.show();
    }

    private void loadElectionGrid(Election election) {
        Intent intent = new Intent(getApplicationContext(), ElectionGrid.class);
        String json = mGson.toJson(election, Election.class);
        intent.putExtra("election_json", json);
        intent.putExtra("election_year","0");
        startActivity(intent);
    }

    private void deleteElection(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final Election election = mElections.get(position);
        builder.setTitle(getString(R.string.delete_dlg_title));
        String warning = String.format(getString(R.string.confirm_delete_format), election.getTitle());
        builder.setMessage(warning);

        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String path = String.format(getString(R.string.election_path_format),mUser.getUid(), election.getTitle() );
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path);
                ref.removeValue();
                mAdapter.removeAt(position);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
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
}
