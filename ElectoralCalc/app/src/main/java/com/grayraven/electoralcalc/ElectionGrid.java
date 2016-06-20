package com.grayraven.electoralcalc;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ElectionGrid extends AppCompatActivity {
    private static final String TAG = "theGrid";
    TableLayout mTable;
    private static final int OPTION_MENU_BASE = 0;
    private int mDemVotes = 0;
    private int mRepVotes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_election_grid);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTable  = (TableLayout)findViewById(R.id.election_table);
        final View row=mTable.getChildAt(1);
        row.setClickable(true);
        row.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                Log.d(TAG, "row " + mTable.indexOfChild(row) + " tag: " + (String) row.getTag());
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }
    /* end oncreate*/

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) { //see also invalidateOptionsMenu
        //add(int groupId, int itemId, int order, CharSequence title);
        menu.clear();
        menu.add(Menu.NONE,Menu.NONE,0, "option 0" );
        menu.add(Menu.NONE,Menu.NONE,1, "option 1");


        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void cellClick(View v) {
//Get the id of the clicked object and assign it to a Textview variable
        TextView cell = (TextView) findViewById(v.getId());
        String tag = (String) cell.getTag();
        String name = getResources().getResourceEntryName(cell.getId());
        Log.d(TAG, "Clicked: " + tag + " - " + name); //use name
        if(tag.contains("split")) {
            Log.d(TAG, "ignore " + tag);
            return;
        }
        if(tag.contains("votes"))
        {
            HandleSplitVotes(name,tag);
            return;
        }

        ClearStateCells(name, R.color.white, "", "");

        if(tag.equals("D")){
            cell.setBackgroundResource(R.color.dem_blue);
            Log.d(TAG, "report that state " + name + " voted D");
        } else {
            cell.setBackgroundResource(R.color.rep_red);
            Log.d(TAG, "report that state " + name + " voted R");
        }

    }

    @OnClick(R.id.btn_save)
    public void onSave() {
        Log.d(TAG, "save election");
    }

    private void HandleSplitVotes(final String name, final String tag) {
        Log.d(TAG, "handle split votes for " + tag);
        final Dialog splitDlg = new Dialog(this);
        splitDlg.setContentView(R.layout.vote_split_dlg);
        TextView titleView = (TextView)splitDlg.findViewById(R.id.dlgTitle);
        String title = (String) titleView.getText();
        int maxVotes = 0;
        if(name.contains("ME")) {
            title = "Maine has 4 votes";
            maxVotes = 4;
        }

        if(name.contains("NE")) {
            title = "Nebraska has 5 votes";
            maxVotes=5;
        }
        titleView.setText(title);

        final Button dlgOk = (Button)splitDlg.findViewById(R.id.dlg_ok);
        final int finalMaxVotes = maxVotes;
        dlgOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText demEdit = (EditText) splitDlg.findViewById(R.id.dem_votes);
                EditText repEdit = (EditText) splitDlg.findViewById(R.id.rep_votes);
                String demVotes = String.valueOf(demEdit.getText());
                String repVotes = String.valueOf(repEdit.getText());
                Log.d(TAG, "dem: " + demVotes + " - rep: " + repVotes);
                if(Integer.parseInt(demVotes) + Integer.parseInt(repVotes) != finalMaxVotes){
                    String error = "Please enter " + Integer.toString(finalMaxVotes) + " total votes";
                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
                    return;
                }

                SaveSplitVote(name, tag, demVotes, repVotes);
                splitDlg.dismiss();
            }

          /*  private void SaveSplitVote(String tag, String demVotes, String repVotes) {
                Log.d(TAG, "Report " + tag + " spilt vote: " + "D: " + demVotes + " - R: " + repVotes );
            }*/
        });

        final Button dlogCancel = (Button)splitDlg.findViewById(R.id.dlg_cancel);
        dlogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                splitDlg.dismiss();
            }
        });

        splitDlg.show();

    }

    private void SaveSplitVote(String name, String tag, String demVotes, String repVotes) {
        Log.d(TAG, "split name: " + name);
        Log.d(TAG, "Report " + tag + " spilt vote: " + "D: " + demVotes + " - R: " + repVotes );
        ClearStateCells(name, R.color.purple, demVotes, repVotes);
    }

    private void  ClearStateCells(String name, int colorId, String demVotes, String repVotes) {
        String thisRow = "";
        for(String token : name.split("_")) {
            thisRow = token; // row is the last token
        }
        int nRow = Integer.parseInt(thisRow);
        TableRow tRow =(TableRow) mTable.getChildAt(nRow);
        TextView tview1 = (TextView)tRow.getChildAt(1);
        if(!demVotes.isEmpty()){
            tview1.setText(demVotes);
        }

        tview1.setBackgroundResource(colorId);
        TextView tview2 = (TextView)tRow.getChildAt(2);
        if(!repVotes.isEmpty()){
            tview2.setText(repVotes);
        }
        tview2.setBackgroundResource(colorId);
    }

}

//horizontal line:
//http://stackoverflow.com/questions/5049852/android-drawing-separator-divider-line-in-layout
