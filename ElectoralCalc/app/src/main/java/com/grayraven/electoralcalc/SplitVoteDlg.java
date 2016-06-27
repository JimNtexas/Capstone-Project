package com.grayraven.electoralcalc;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.grayraven.electoralcalc.PoJos.SplitVoteResultMsg;

import org.greenrobot.eventbus.EventBus;

/**
 * Dialog to handle the special cases of Nebraska and Maine; these states can split their electoral college votes
 */
public class SplitVoteDlg extends DialogFragment {

    private static final String TAG = "VoteDlg";
    private int mDemvotes;
    private int mRepvotes;
    private int mMaxvotes;
    private EditText mDemEdit;
    private EditText mRepEdit;
    private String mState;
    private int mRow;

    public SplitVoteDlg(){}

    public static SplitVoteDlg newInstance(String title,int row, int maxVotes, String state){
        SplitVoteDlg fragment = new SplitVoteDlg();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("max_votes",maxVotes);
        args.putString("state", state);
        args.putInt("row", row);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.vote_split_dlg, container);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String title = getArguments().getString("title");
        mMaxvotes = getArguments().getInt("max_votes");
        mState = getArguments().getString("state");
        mRow = getArguments().getInt("row");

        ((TextView) view.findViewById(R.id.dlgTitle)).setText(title);
        mDemEdit = (EditText) view.findViewById(R.id.dem_votes);
        mRepEdit = (EditText) view.findViewById(R.id.rep_votes);

        //handle soft enter
        mRepEdit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            normalClose();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });


        Button btnSave = (Button) view.findViewById(R.id.dlg_ok);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                normalClose();
            }
        });

        Button btnCancel = (Button) view.findViewById(R.id.dlg_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                dismiss();
            }
        });
    }

    private void normalClose() {
        mDemvotes= Integer.parseInt(String.valueOf(mDemEdit.getText()));
        mRepvotes = Integer.parseInt(String.valueOf(mRepEdit.getText()));

        if(mDemvotes + mRepvotes != mMaxvotes) {
            Toast.makeText(getActivity(),getString(R.string.too_many_votes), Toast.LENGTH_LONG).show();
            return;
        }
        EventBus.getDefault().post(new SplitVoteResultMsg(mState, mRow, mDemvotes, mRepvotes));
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dismiss();

    }

}


