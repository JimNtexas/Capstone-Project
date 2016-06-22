package com.grayraven.electoralcalc.PoJos;

public class State {

    private String abbr;
    private String name;
    private int votes;
    private boolean splitable = false;
    private int dems;
    private int reps;
    private int third = 0; //third parties not yet supported

    public State() {
    }

    public State(String abbr, String name,  boolean splitable, int dems,  int reps, int third, int votes) {
        this.abbr = abbr;
        this.dems = dems;
        this.name = name;
        this.reps = reps;
        this.splitable = splitable;
        this.third = third;
        this.votes = votes;
    }

    public void copy(State copy){
        this.abbr = copy.abbr;
        this.dems = copy.dems;
        this.name = copy.name;
        this.reps = copy.reps;
        this.splitable = copy.splitable;
        this.third = copy.third;
        this.votes = copy.votes;
    }

    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }

    public int getDems() {
        return dems;
    }

    public void setDems(int dems) {
        this.dems = dems;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public boolean isSplitable() {
        return splitable;
    }

    public void setSplitable(boolean splitable) {
        this.splitable = splitable;
    }

    public int getThird() {
        return third;
    }

    public void setThird(int third) {
        this.third = third;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    @Override
    public String toString() {
        return "State{" +
                "abbr='" + abbr + '\'' +
                ", name='" + name + '\'' +
                ", votes=" + votes +
                ", splitable=" + splitable +
                ", dems=" + dems +
                ", reps=" + reps +
                ", third=" + third +
                '}';
    }

}
