package com.lenovo.adminmatchpoint;

import java.util.Date;

/**
 * Created by atharva vyas on 29-06-2017.
 * part of tournament ms data
 */

public class TournamentMatch {
    public String player1;
    public String player2;
    public String matchScores;
    public String winner;
    public String matchType;
    public Integer matchid;
    public TournamentMatch()
    {

    }
    public TournamentMatch(String p1,String p2,String ms,String winner,String mt,int mid)
    {
        this.player1=p1;
        this.player2=p2;
        this.matchScores=ms;
        this.winner=winner;
        this.matchType=mt;
        this.matchid=mid;
    }
}
