package com.grayraven.electoralcalc.PoJos;

/**
 * Created by Jim on 6/10/2016.
 */
public class StateData {

    private String State, Dem, Rep;
    private int votes;

    public StateData() {
    }

    public String getDem() {
        return Dem;
    }

    public void setDem(String dem) {
        Dem = dem;
    }

    public String getRep() {
        return Rep;
    }

    public void setRep(String rep) {
        Rep = rep;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    @Override
    public String toString() {
        return "StateData{" +
                "Dem='" + Dem + '\'' +
                ", State='" + State + '\'' +
                ", Rep='" + Rep + '\'' +
                ", votes=" + votes +
                '}';
    }


}
