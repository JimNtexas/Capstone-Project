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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.grayraven.electoralcalc.PoJos.Election;
import com.grayraven.electoralcalc.PoJos.Utilities;

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

    private RecyclerView recyclerView;
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
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new ElectionAdapter(mElections);
        recyclerView.setAdapter(mAdapter);

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

   /* private void adFakeElections() {
        Election e1 = new Election();
        e1.setTitle("one");
        Election e2 = new Election();
        e2.setTitle("two");
        Election e3 = new Election();
        e3.setTitle("three");

        mElections.add(e1);
        mElections.add(e2);
        mElections.add(e3);
        mAdapter.notifyDataSetChanged();

    }*/


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
                startActivity(intent);
                dialog.dismiss();
            }
        });
        chooseYearDlg.show();
    }
}
