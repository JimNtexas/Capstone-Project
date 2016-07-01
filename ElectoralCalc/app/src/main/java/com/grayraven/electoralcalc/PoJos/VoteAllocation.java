package com.grayraven.electoralcalc.PoJos;

/**
 * Models the U.S. decennial adjustments to the allocations of Electoral College Votes
 */
public class VoteAllocation
{
    public VoteAllocation(){}
    public VoteAllocation(String abv, String votes){
        this.abv = abv;
        this.votes = votes;
    }

    private String abv;

    private String votes;

    public String getAbv ()
    {
        return abv;
    }

    public void setAbv (String abv)
    {
        this.abv = abv;
    }

    public String getVotes ()
    {
        return votes;
    }

    public void setVotes (String votes)
    {
        this.votes = votes;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [abv = "+abv+", votes = "+votes+"]";
    }
}