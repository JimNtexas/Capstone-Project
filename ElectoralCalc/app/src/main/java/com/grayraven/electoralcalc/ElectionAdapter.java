package com.grayraven.electoralcalc;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grayraven.electoralcalc.PoJos.Election;

import java.util.List;

public class ElectionAdapter  extends RecyclerView.Adapter<ElectionAdapter.ElectionViewHolder> {


    private List<Election> mElections;

    public class ElectionViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre;

        public ElectionViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
        }
    }


    public ElectionAdapter(List<Election> mElections) {
        this.mElections = mElections;
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
    }

    @Override
    public int getItemCount() {
        return mElections.size();
    }
}

    
    
