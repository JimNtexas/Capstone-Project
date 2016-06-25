package com.grayraven.electoralcalc;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.grayraven.electoralcalc.PoJos.Election;
import com.grayraven.electoralcalc.PoJos.RecycleViewClickMsg;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class ElectionAdapter  extends RecyclerView.Adapter<ElectionAdapter.ElectionViewHolder> {
    private static final String TAG = "Adapter";

    private List<Election> mElections;

    public void removeAt(int position){
        mElections.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mElections.size());
    }

    public class ElectionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView title;
        public TextView year;
        ImageButton button;
        private String mTitle;

        public ElectionViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.summary);
            mTitle = (String) title.getText();
            title.setOnClickListener(this);
            year = (TextView) view.findViewById(R.id.year);
            year.setOnClickListener(this);
            button = (ImageButton) view.findViewById(R.id.trashCan);
            button.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "postion: " + getPosition());
           switch(v.getId()){
               case R.id.summary:
               case R.id.year:
                   EventBus.getDefault().post(new RecycleViewClickMsg(RecycleViewClickMsg.OPEN_ELECTION, getPosition()));
                   break;
               case R.id.trashCan:
                   EventBus.getDefault().post(new RecycleViewClickMsg(RecycleViewClickMsg.DELETE_ELECTION, getPosition()));
                   break;
           }
        }
    }



    public ElectionAdapter(List<Election> elections) {
        this.mElections = elections;
    }

    @Override
    public ElectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.election_list_row, parent, false);
        return new ElectionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ElectionViewHolder holder, int position) {
        Election election = mElections.get(position);
        holder.title.setText(election.getTitle());
        holder.year.setText(String.valueOf(election.getYear()));
    }


    @Override
    public int getItemCount() {
        return mElections.size();
    }
}

    
    
