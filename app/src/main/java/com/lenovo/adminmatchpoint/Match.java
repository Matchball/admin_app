package com.lenovo.adminmatchpoint;

/**
 * Created by atharva on 28-06-2017.
 */

public class Match {
    public Integer player1id;
    public Integer player2id;
    public String matchType;
    public Match(){

    }
    public Match(String mt)
    {
        this.matchType=mt;
    }
    public Match(int p1,int p2,String mt)
    {
        this.player1id=p1;
        this.player2id=p2;
        this.matchType=mt;
    }
}
