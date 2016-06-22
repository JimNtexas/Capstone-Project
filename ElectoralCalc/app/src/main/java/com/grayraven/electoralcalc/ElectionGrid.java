package com.grayraven.electoralcalc;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


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
                Log.d(TAG, "row " + mTable.indexOfChild(row) + " tag: " + (String) row.getTag());
            }
        });

        mElectionYear = Integer.parseInt(getIntent().getStringExtra("election_year"));
        initElectionData(mElectionYear);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }
    /* end oncreate*/

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) { //see also invalidateOptionsMenu
        //add(int groupId, int itemId, int order, CharSequence title);
        menu.clear();
        menu.add(Menu.NONE, Menu.NONE, 0, "option 0");
        menu.add(Menu.NONE, Menu.NONE, 1, "option 1");


        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return false;

        }
        return super.onOptionsItemSelected(item);
    }

    /* ------------ initialization ------------*/
    private void initElectionData(int year) {
        initVoteAllocations(year);
        initStates();
        initGrid(true); //todo: use shared prefs
    }
    //Lists that contain the decennial allocation of electoral college votes by state
    private void initVoteAllocations(int year) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<VoteAllocation>>() {
        }.getType();

        switch(year) {
            case 2000 : mAllocations = (ArrayList<VoteAllocation>) gson.fromJson(VoteAllocations.Votes1990, listType);
                break;
            case 2004:
            case 2008:  mAllocations = (ArrayList<VoteAllocation>) gson.fromJson(VoteAllocations.Votes2000, listType);
                break;
            case 2012:
            case 2016:  mAllocations = (ArrayList<VoteAllocation>) gson.fromJson(VoteAllocations.Votes2010, listType);
                break;
            default:
            Log.e(TAG, "Nonsupported election year! - " + year);
        }
    }

    private void initGrid(boolean byName) {

        int row = 1; // row zero is the headers
        for (State s : mStateList ) {
            String state = s.getAbbr();
            String votes = Integer.toString(s.getVotes());
            TableRow tRow = (TableRow) mTable.getChildAt(row);
            TextView tview1 = (TextView) tRow.getChildAt(0);
            tview1.setText(state + "-" + votes);
            if(state.contains("ME") || state.contains("NE")) //split vote states
            {
                TextView split = (TextView)tRow.getChildAt(3);
                split.setText(R.string.txt_split);
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
    /* ------------ end initialization -----------*/

    @OnClick(R.id.btn_save)
    protected void saveElection() {
        if(mElection == null) {
            mElection = new Election();
        }
        mElection.setStates(mStateList);
        String title = electionTitle.getText().toString();
        mElection.setTitle(title);
        mElection.setYear(mElectionYear);
        Log.d(TAG, "Saveing Election: " + mElection.toString());

    }

    private void saveState(State updatedState) {
        int cnt = 0;
        for(State state : mStateList){
            if(mStateList.get(cnt).getAbbr().compareTo(updatedState.getAbbr()) == 0) {
                mStateList.get(cnt).copy(updatedState);
                updateVoteTotals(state);
                return;
            }
            cnt++;
        }
    }

    private void updateVoteTotals(State state) {
        int currentDemVotes = Integer.parseInt(demTotalVotes.getText().toString()) + state.getDems();
        int currentRepVotes = Integer.parseInt(repTotalVotes.getText().toString()) + state.getReps();
        demTotalVotes.setText(Integer.toString(currentDemVotes));
        repTotalVotes.setText(Integer.toString(currentRepVotes));
        if(currentDemVotes >= 270) {
            demTotalVotes.setBackgroundResource(R.color.light_green);
        } else if(currentRepVotes >= 270) {
            repTotalVotes.setBackgroundResource(R.color.light_green);
        }
    }

    //Get the id of the clicked object and assign it to a Textview variable
    public void cellClick(View v) {
        TextView cell = (TextView) findViewById(v.getId());
        String tag = (String) cell.getTag(); //ex: D-6

        String[] tokens = tag.split("-");
        String sRow = tokens[1];
        int index = Integer.parseInt(sRow) - 1; //ignore header row todo: adjust content_election_grid generator script so that this isn't needed
        String name = mAllocations.get(index).getAbv();

        if (tag.contains("split")) {
           // HandleSplitVotes(name, sRow);
            HandleSplitVotesDialog(name,sRow);
            return;
        }

        ClearStateCells(Integer.parseInt(sRow), R.color.white, "", "");

        State state = getStateByAbbreviation(name);
        if (tag.contains("D")) {
            cell.setBackgroundResource(R.color.dem_blue);
            state.setDems(state.getVotes());

        } else {
            cell.setBackgroundResource(R.color.rep_red);
            state.setReps(state.getVotes());
        }
        saveState(state);
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


    /* ------------ utilities -----------*/
    private void sortStatesByAbbreviation(ArrayList<State> list) {
        Collections.sort(list, new Comparator<State>() {
            public int compare(State a1, State a2) {
                return a1.getAbbr().compareTo(a2.getAbbr());
            }
        });
    }

    private void sortStatesByVotes(ArrayList<State> list) {
        Collections.sort(list, new Comparator<State>() {
            public int compare(State a1, State a2) {
                int a = a1.getVotes();
                int b = a2.getVotes();
                return b - a; // use a - b to sort low to high
            }
        });
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



     /* ------------ end utilities --------*/



    @Override
    public void onBackPressed() {
        Log.d(TAG, "onback");
        super.onBackPressed();

    }


    @Override
    protected void onPause() {
        super.onPause();
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


