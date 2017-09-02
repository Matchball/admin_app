package com.lenovo.adminmatchpoint;

import java.util.Map;

import static android.R.attr.y;

/**
 * Created by atharva vyas on 14-07-2017.
 * Used to store ranking of a particular month
 */
public class RankListData {
    public int month;
    public int year;
    public Map<String,MonthlyRankData> rankmap;
    public RankListData()
    {

    }
    public RankListData(int m,int y,Map<String,MonthlyRankData> rankmap)
    {
        this.month=m;
        this.year=y;
        this.rankmap=rankmap;
    }
}
