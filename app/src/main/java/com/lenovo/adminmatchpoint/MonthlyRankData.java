package com.lenovo.adminmatchpoint;

/**
 * Created by atharva vyas on 14-07-2017.
 * sub part of rank list data
 */

public class MonthlyRankData {
    public Long rank;
    public String playercategory;
    public String city;
    public MonthlyRankData()
    {

    }
    public MonthlyRankData(long r,String p,String c){
        this.rank=r;
        this.playercategory=p;
        this.city=c;
    }
}
