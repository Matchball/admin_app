package com.lenovo.adminmatchpoint;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by atharva vyas on 29-06-2017.
 * contains match data for a particular category of tournament
 */

public class TournamentMSData {
    public String tournamentName;
    public Date entryDate;
    /**
     * list of all tournament matches for a the particular tournament and category
     */
    public ArrayList<TournamentMatch> matchlist;
    public ArrayList<String> enrolled;
    public long time;
    /**
     * Playr id is key and points alloted at the ned of tournament is key
     */
    public Map<String,Integer> pointallotment;
    public String category;
    public long rounds;
    public TournamentMSData()
    {

    }
    public TournamentMSData(String tname,Date edate,ArrayList<TournamentMatch> ml,ArrayList<String> enrolled,Map<String,Integer>pallotment,long rounds,String category)
    {
        this.tournamentName=tname;
        this.entryDate=edate;
        this.matchlist=ml;
        this.enrolled=enrolled;
        this.time=-1*edate.getTime();
        this.pointallotment=pallotment;
        this.rounds=rounds;
        this.category=category;
    }
}
