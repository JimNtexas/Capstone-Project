package com.grayraven.electoralcalc;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

import java.util.HashMap;

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

    //total votes per state are allocated based on the U.S. decennial census
    HashMap<String, Integer> mAllocationMap2000 = new HashMap<String, Integer>();
    HashMap<String, Integer> mAllocationMap1990 = new HashMap<String, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
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

         /* ---- Firebase data changes ----*/
        mListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Value event change cnt: " + dataSnapshot.getChildrenCount());
            //    DataSnapshot elections = dataSnapshot.child("users/" + mUser.getUid() + "/elections");
                DataSnapshot elections = dataSnapshot.child("Votes1996");
                Log.d(TAG, "election count: " + elections.getChildrenCount());
                for(DataSnapshot  el: elections.getChildren()){
                    Log.d(TAG, "key  :" +el.getKey());
                 //   Election e = getElection(el.getValue().toString());
                    Log.d(TAG, "value: " + el.getValue().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "FB DatabaseError " + databaseError.getMessage()); // probably the user is not logged in
                showLoginScreen();

            }
        };

        FirebaseDatabase.getInstance().getReference().addValueEventListener(mListener);
    } // end onCreate

    private void handleDatabaseError() {

    }

    private void showLoginScreen() {
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
