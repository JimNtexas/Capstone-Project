package com.grayraven.electoralcalc.PoJos;
// used to pass results of split vote dialog back to the Election grid
public class SplitVoteResultMsg {
    public String state = "";
    public int demVotes = 0;
    public int repVotes = 0;
    public int row = 0;

    public SplitVoteResultMsg(String state, int row,  int demVotes, int repVotes) {
        this.state = state;
        this.row = row;
        this.demVotes = demVotes;
        this.repVotes = repVotes;
    }
}


