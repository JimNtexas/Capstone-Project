package com.grayraven.electoralcalc.PoJos;

public class RecycleViewClickMsg {
    public static final String OPEN_ELECTION = "open_election";
    public static final String DELETE_ELECTION = "delete_election";
    private String mAction;
    private int mPostion;

    public String getAction() { return mAction;}
    public int getPosition() { return mPostion;}

    public RecycleViewClickMsg(String action, int position) {
        mAction = action;
        mPostion = position;
    }
}
