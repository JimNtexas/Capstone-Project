package com.grayraven.electoralcalc;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.grayraven.electoralcalc.PoJos.SplitVoteResultMsg;
import com.grayraven.electoralcalc.PoJos.VoteAllocation;
import com.grayraven.electoralcalc.PoJos.VoteAllocations;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class ElectionGrid extends AppCompatActivity {
    private static final String TAG = "theGrid";
    TableLayout mTable;
    private static final int OPTION_MENU_BASE = 0;
    private int mDemVotes = 0;
    private int mRepVotes = 0;
    ArrayList<VoteAllocation> mAllocation2000;
    ArrayList<VoteAllocation> mallocation1990;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_election_grid);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        initVoteAllocations();
       

        mTable  = (TableLayout)findViewById(R.id.election_table);
        final View row=mTable.getChildAt(1);
        row.setClickable(true);
        row.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                Log.d(TAG, "row " + mTable.indexOfChild(row) + " tag: " + (String) row.getTag());
            }
        });

        initGrid(true);

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
        return super.onOptionsItemSelected(item);
    }

    public void cellClick(View v) {
//Get the id of the clicked object and assign it to a Textview variable
        TextView cell = (TextView) findViewById(v.getId());
        String tag = (String) cell.getTag(); //ex: D-6
        //String id = getResources().getResourceEntryName(cell.getId()); //ex D-6

        String[] tokens = tag.split("-");
        String sRow = tokens[1];
        int index = Integer.parseInt(sRow) - 1; //ignore header row todo: adjust content_election_grid generator script so that this isn't needed
        String name = mAllocation2000.get(index).getAbv();

        if (tag.contains("split")) {
           // HandleSplitVotes(name, sRow);
            HandleSplitVotesDialog(name,sRow);
            return;
        }

        ClearStateCells(Integer.parseInt(sRow), R.color.white, "", "");
        if (tag.contains("D")) {
            cell.setBackgroundResource(R.color.dem_blue);
            Log.d(TAG, "report that state " + index + ": " + name + " voted D");
        } else {
            cell.setBackgroundResource(R.color.rep_red);
            Log.d(TAG, "report that state " + sRow + ": " + name + " voted R");
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

        String title = String.format(getString(R.string.split_dlg_title),name, maxVotes);
        SplitVoteDlg dlg = SplitVoteDlg.newInstance(title,Integer.parseInt(sRow), maxVotes, abbr);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        dlg.show(transaction,TAG);
    }

    @Subscribe
    public void onSplitVoteResultMsg(SplitVoteResultMsg msg) {
        Log.d(TAG, "report split vote: " + msg.state + " D: " + msg.demVotes + " R: " + msg.repVotes);
        SaveSplitVote(msg.state, msg.row, Integer.toString(msg.demVotes),Integer.toString(msg.repVotes));
    }


 /*   private void HandleSplitVotes(final String name, final String sRow) {
        Log.d(TAG, "handle split votes for " + name + "row " + sRow);
        final Dialog splitDlg = new Dialog(this);
        splitDlg.setContentView(R.layout.vote_split_dlg);
        TextView titleView = (TextView) splitDlg.findViewById(R.id.dlgTitle);
        String title = (String) titleView.getText();
        int maxVotes = 0;
        if (name.contains("ME")) {
            title = "Maine has 4 votes";
            maxVotes = 4;
        }

        if (name.contains("NE")) {
            title = "Nebraska has 5 votes";
            maxVotes = 5;
        }
        titleView.setText(title);

        final Button dlgOk = (Button) splitDlg.findViewById(R.id.dlg_ok);
        final int finalMaxVotes = maxVotes;
        dlgOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText demEdit = (EditText) splitDlg.findViewById(R.id.dem_votes);
                EditText repEdit = (EditText) splitDlg.findViewById(R.id.rep_votes);
                String demVotes = String.valueOf(demEdit.getText());
                String repVotes = String.valueOf(repEdit.getText());
                Log.d(TAG, "dem: " + demVotes + " - rep: " + repVotes);
                if (Integer.parseInt(demVotes) + Integer.parseInt(repVotes) != finalMaxVotes) {
                    String error = String.format(getString(R.string.too_many_votes), finalMaxVotes);
                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
                    return;
                }

                SaveSplitVote(name, sRow, demVotes, repVotes);

                splitDlg.dismiss();
            }

        });

        final Button dlogCancel = (Button) splitDlg.findViewById(R.id.dlg_cancel);
        dlogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                splitDlg.dismiss();
            }
        });

        splitDlg.show();

    }*/

    private void SaveSplitVote(String name, int row, String demVotes, String repVotes) {
        Log.d(TAG, "split name: " + name);
        Log.d(TAG, "Report " + row + " spilt vote: " + "D: " + demVotes + " - R: " + repVotes);
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

    //Lists that contain the decennial allocation of electoral college votes by state
    private void initVoteAllocations() {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<VoteAllocation>>() {
        }.getType();
        mAllocation2000 = (ArrayList<VoteAllocation>) gson.fromJson(VoteAllocations.Votes2000, listType);
        mallocation1990 = (ArrayList<VoteAllocation>) gson.fromJson(VoteAllocations.Votes1990, listType);
    }

    private void sortAllocationsByState(ArrayList<VoteAllocation> list) {
        Collections.sort(list, new Comparator<VoteAllocation>() {
            public int compare(VoteAllocation a1, VoteAllocation a2) {
                return a1.getAbv().compareTo(a2.getAbv());
            }
        });
    }

    private void sortAllocationsByVotes(ArrayList<VoteAllocation> list) {
        Collections.sort(list, new Comparator<VoteAllocation>() {
            public int compare(VoteAllocation a1, VoteAllocation a2) {
                int a = Integer.parseInt(a1.getVotes());
                int b = Integer.parseInt(a2.getVotes());
                return b - a; // use a - b to sort low to high
            }
        });
    }

    private void initGrid(boolean byName) {

        int row = 1; // row zero is the headers
        for (VoteAllocation alloc : mAllocation2000 ) {
            String state = alloc.getAbv();
            String votes = alloc.getVotes();
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


