package com.grayraven.electoralcalc;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.grayraven.electoralcalc.PoJos.Election;
import com.grayraven.electoralcalc.PoJos.SplitVoteResultMsg;
import com.grayraven.electoralcalc.PoJos.State;
import com.grayraven.electoralcalc.PoJos.StateData;
import com.grayraven.electoralcalc.PoJos.VoteAllocation;
import com.grayraven.electoralcalc.PoJos.VoteAllocations;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


@SuppressWarnings("unused")
public class ElectionGrid extends AppCompatActivity {
    private static final String TAG = "theGrid";
    TableLayout mTable;
    private static final int OPTION_MENU_BASE = 0;
    private int mDemVotes = 0;
    private int mRepVotes = 0;
    ArrayList<VoteAllocation> mAllocations;
    ArrayList<State> mStateList = new ArrayList<State>();
    private int mCensusYear = 2010;
    private int mElectionYear;
    private boolean mDirty = false;
    private Election mElection = null;
    @BindView(R.id.election_title) EditText electionTitle;
    @BindView(R.id.dem_total_votes) TextView demTotalVotes;
    @BindView(R.id.rep_total_votes) TextView repTotalVotes;
    @BindView(R.id.election_year) TextView electionYear;
    ProgressDialog mProgress;
    Gson mGson = new Gson();
    HashMap<String,String> mColorMap = new HashMap<String,String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_election_grid);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        mTable  = (TableLayout)findViewById(R.id.election_table);
        final View row=mTable.getChildAt(1);
        row.setClickable(true);
        row.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                Log.d(TAG, "row " + mTable.indexOfChild(row) + " tag: " + row.getTag());
            }
        });

        String strYear = getIntent().getStringExtra("election_year");
        mElectionYear = Integer.parseInt(strYear);
        if(mElectionYear > 0) {
            initElectionData(mElectionYear);
        } else {
            String json = getIntent().getStringExtra("election_json");
            initElectionData(json);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mColorMap.clear();

    }

    /* end oncreate*/


    /* ------------ initialization ------------*/
    private void initElectionData(int year) {
        initVoteAllocations(year);
        initStates();
        initGrid(true);
        String strYear = Integer.toString(mElectionYear);
        electionYear.setText(strYear);
    }

    private void initElectionData(String json) {
        Election election = mGson.fromJson(json, Election.class);
        initVoteAllocations(election.getYear());
        mElection = new Election(election.getTitle(),election.getRemark(),election.getYear(),election.getStates(), election.getLocked());
        initStates(election);
        initGrid(true);
        electionTitle.setText(election.getTitle());
        String strYear = Integer.toString(election.getYear());
        electionYear.setText(strYear);
    }

    //Lists that contain the decennial allocation of electoral college votes by state
    private void initVoteAllocations(int year) {
        Type listType = new TypeToken<List<VoteAllocation>>() {
        }.getType();

        switch(year) {
            case 2000 : mAllocations =  mGson.fromJson(VoteAllocations.Votes1990, listType);
                Log.d(TAG, "year 1990 data");
                break;
            case 2004:
            case 2008:  mAllocations =  mGson.fromJson(VoteAllocations.Votes2000, listType);
                Log.d(TAG, "year 2000 data");
                break;
            case 2012:
            case 2016:  mAllocations =  mGson.fromJson(VoteAllocations.Votes2010, listType);
                Log.d(TAG, "year 2010 data");
                break;
            default:
            Log.e(TAG, "Nonsupported election year! - " + year);
        }
    }



    //TODO:  SET COLORS IF REQUIRED
    private void initGrid(boolean byName) {

        int row = 1; // row zero is the headers
        for (State s : mStateList ) {
            String state = s.getAbbr();
            String votes = Integer.toString(s.getVotes());
            TableRow tRow = (TableRow) mTable.getChildAt(row);
            for(int i=0; i<4; i++){
                ((TextView)(tRow.getChildAt(i))).setTextSize(21);
            }

            TextView tview1 = (TextView) tRow.getChildAt(0);
            tview1.setText(state + "-" + votes);
            if(state.contains("ME") || state.contains("NE")) //split vote states
            {
                TextView split = (TextView)tRow.getChildAt(3);
                split.setText(R.string.txt_split);
            }
            State current = mStateList.get(row-1);
            if(current.getReps() > 0 || current.getDems() > 0){
                // set state color
             //   Log.d(TAG, "State: " + current.getAbbr() + " - Dems: " + current.getDems()+ " - Reps: " + current.getReps()  );
                TextView demCell = (TextView)tRow.getChildAt(1);
                TextView repCell = (TextView)tRow.getChildAt(2);
                if(current.getReps() == 0 && current.getDems() >0 ) {
                    // blue state
                    demCell.setBackgroundResource(R.color.dem_blue);
                } else if(current.getReps() > 0 && current.getDems() == 0){
                    // red state
                    repCell.setBackgroundResource(R.color.rep_red);
                } else if(current.getDems() >0 && current.getReps()> 0) {
                    //split vote state
                    demCell.setBackgroundResource(R.color.purple);
                    repCell.setBackgroundResource(R.color.purple);
                }
            }

            row++;
        }
    }


    private void initStates() {
        int cnt = 0;
        for(String abv : StateData.Abbreviations){
            State state = new State();
            state.setAbbr(abv);
            state.setName(StateData.Names[cnt]);
            state.setVotes( Integer.parseInt(mAllocations.get(cnt).getVotes()));
            mStateList.add(state);
            cnt++;
        }
    }

    private void initStates(Election election){
        mElectionYear = election.getYear();
        int repTotal = 0;
        int demTotal = 0;
        int cnt =  0;
        for(String abv : StateData.Abbreviations){
            String abbr = election.getStates().get(cnt).getAbbr();
            int dems = election.getStates().get(cnt).getDems();
            int reps = election.getStates().get(cnt).getReps();

            State state = new State();
            state.setAbbr( abbr );
            state.setName(StateData.Names[cnt]);
            state.setVotes( Integer.parseInt(mAllocations.get(cnt).getVotes()));
            state.setDems(dems);
            state.setReps(reps);
            repTotal += state.getReps();
            demTotal += state.getDems();
            mStateList.add(state);
            cnt++;
        }
        demTotalVotes.setText(Integer.toString(demTotal));
        repTotalVotes.setText(Integer.toString(repTotal));
        setWinner();
    }

    private void setWinner() {
        int demVotes = Integer.parseInt((String)demTotalVotes.getText());
        int repVotes = Integer.parseInt((String)repTotalVotes.getText());
        if(demVotes >= 270) {
            demTotalVotes.setBackgroundResource(R.color.light_green);
        } else if(repVotes >= 270) {
            repTotalVotes.setBackgroundResource(R.color.light_green);
        }
    }
    /* ------------ end initialization -----------*/

    @OnClick(R.id.btn_save)
    protected void saveElection() {
        FirebaseAuth auth = FirebaseAuth.getInstance();



        if(mElection == null) {
            mElection = new Election();
        }  else {
            if (mElection.getLocked()) {
                if(mElection.getTitle().compareTo( electionTitle.getText().toString()) == 0) {

                    lockedElectionDlg();
                    return;
                } else {
                    mElection.setLocked(false);
                }
            }
        }
        mProgress = ProgressDialog.show(ElectionGrid.this, "",
                getString(R.string.saving_election), true);
        mProgress.show();
        mElection.setStates(mStateList);
        String title = electionTitle.getText().toString().trim();
        mElection.setTitle(title);
        mElection.setYear(mElectionYear);

        Gson gson = new Gson();
        final String json = gson.toJson(mElection);
        //Log.d(TAG,"json: " + json);
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        String uid = auth.getCurrentUser().getUid();
       // String path = String.format(getString(R.string.election_path_format),uid, mElection.getTitle());
        //////////////////////////////////
        mElection.setLocked(true);
        String path ="/PastResults/" + mElection.getTitle();
        //////////////////////////////////
        final DatabaseReference dbRef = db.getReference(path);
        //check if this election already exists
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mProgress.dismiss();
                if(dataSnapshot.getValue() == null) {
                    dbRef.setValue(json);
                    Snackbar.make(findViewById(R.id.election_grid),getString(R.string.election_saved), Snackbar.LENGTH_LONG).show();
                } else {
                    Log.d(TAG, "exists: " + dataSnapshot.getKey());
                    dbOverwriteDlg(dbRef, json);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Snackbar.make(findViewById(R.id.election_grid),getString(R.string.database_error), Snackbar.LENGTH_LONG).show();
                mProgress.dismiss();
            }
        });

    }

    private void updateVoteTotals() {
        int demTotal = 0;
        int repTotal = 0;

        for(State state : mStateList ){
            demTotal += state.getDems();
            repTotal += state.getReps();
        }
        Log.d(TAG, "dems: " + demTotal + " - Reps: " + repTotal);
        demTotalVotes.setText( Integer.toString(demTotal) );
        repTotalVotes.setText( Integer.toString(repTotal) );
        setWinner();
    }

    //Get the id of the clicked object and assign it to a Textview variable
    public void cellClick(View v) {
        TextView cell = (TextView) findViewById(v.getId());
        String tag = (String) cell.getTag(); //ex: D-6

        String[] tokens = tag.split("-");
        String sRow = tokens[1];
        int index = Integer.parseInt(sRow) - 1; //ignore header row
        String name = mAllocations.get(index).getAbv();
        String text = cell.getText().toString();
        if (text.contains("Split")) {
            HandleSplitVotesDialog(name,sRow);
            return;
        }

        if(text.contains(" - ")){
            return;
        }


        ClearStateCells(Integer.parseInt(sRow), R.color.white, "", "");
        State state = getStateByAbbreviation(name);

        //if user clicks on an already colored cell, just set it back to white
        if(mColorMap.containsKey(name)) {
            if(tag.contains("D") && mColorMap.get(name).toString().compareTo("blue") == 0){
                //click on blue cell, just turn it white
                cell.setBackgroundResource(R.color.white);
                state.setDems(0);
                saveState(state);
                return;

            } else if (tag.contains("R") && mColorMap.get(name).toString().compareTo("red") == 0){
                //click on red cell, just turn it white
                 cell.setBackgroundResource(R.color.white);
                state.setReps(0);
                saveState(state);
                return;
            }

        }

       //http://ramirezsystems.blogspot.com/2015/02/android-adding-borders-to-views.html


        if (tag.contains("D")) {
            cell.setBackgroundResource(R.color.dem_blue);
            state.setDems(state.getVotes());
            state.setReps(0);
            mColorMap.put(name, "blue");

        } else {
            cell.setBackgroundResource(R.color.rep_red);
            state.setReps(state.getVotes());
            state.setDems(0);
            mColorMap.put(name, "red");
        }
        saveState(state);
    }

    private void saveState(State updatedState) {
        int cnt = 0;
        for(State state : mStateList){
            if(mStateList.get(cnt).getAbbr().compareTo(updatedState.getAbbr()) == 0) {
                mStateList.get(cnt).copy(updatedState);
                updateVoteTotals();
                return;
            }
            cnt++;
        }
    }

    private void HandleSplitVotesDialog(final String name, final String sRow) {
        int maxVotes = 0;
        String abbr = "";
        if (name.contains("ME")) {
            maxVotes = 4;
            abbr = "ME";
        }

        if (name.contains("NE")) {
            maxVotes = 5;
            abbr = "NE";
        }

        //todo:  use full name of state, not just abbr
        String title = String.format(getString(R.string.split_dlg_title),name, maxVotes);
        SplitVoteDlg dlg = SplitVoteDlg.newInstance(title,Integer.parseInt(sRow), maxVotes, abbr);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        dlg.show(transaction,TAG);
    }

    @Subscribe
    public void onSplitVoteResultMsg(SplitVoteResultMsg msg) {
        SaveSplitVote(msg.state, msg.row, Integer.toString(msg.demVotes),Integer.toString(msg.repVotes));
    }



    private void SaveSplitVote(String name, int row, String demVotes, String repVotes) {
        State state = getStateByAbbreviation(name);
        state.setSplitable(true);
        state.setDems(Integer.parseInt(demVotes));
        state.setReps(Integer.parseInt(repVotes));
        saveState(state);
        ClearStateCells(row, R.color.purple, demVotes, repVotes);
    }

    private void ClearStateCells(int row, int colorId, String demVotes, String repVotes) {
        TableRow tRow = (TableRow) mTable.getChildAt(row);
        TextView tview1 = (TextView) tRow.getChildAt(1);
        if (!demVotes.isEmpty()) {
            tview1.setText(demVotes);
        }

        tview1.setBackgroundResource(colorId);
        TextView tview2 = (TextView) tRow.getChildAt(2);
        if (!repVotes.isEmpty()) {
            tview2.setText(repVotes);
        }
        tview2.setBackgroundResource(colorId);
    }


    private State getStateByAbbreviation(String abbr){
        for(State state : mStateList){
            if(state.getAbbr().compareTo(abbr) == 0) {
                return state;
            }
        }
        Log.e(TAG, "getStateByAbbreviation called invalid state " + abbr + "!");
        return null;
    }


    private void dbOverwriteDlg(final DatabaseReference dbRef, String jsn) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final String json = jsn;
        String title = String.format(getString(R.string.overwrite_title_format), dbRef.getKey() );
        builder.setTitle(title);
        builder.setMessage(getString(R.string.are_you_sure));

        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dbRef.setValue(json);
                Snackbar.make(findViewById(R.id.election_grid),getString(R.string.election_saved), Snackbar.LENGTH_LONG).show();
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

    private void lockedElectionDlg(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.locked_dlg_title)
                .setCancelable(false)
                .setPositiveButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        electionTitle.requestFocus();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onback");
        //todo: warn if file is dirty
       // super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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


