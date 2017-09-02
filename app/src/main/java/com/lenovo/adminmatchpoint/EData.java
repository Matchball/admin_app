package com.lenovo.adminmatchpoint;

/**
 * Created by atharva vyas on 24-06-2017.
 */

public class EData implements Comparable<EData>
{
    public String playerid;
    public Long rank;
    public EData()
    {

    }
    public EData(String pid,Long r)
    {
        this.playerid=pid;
        this.rank=r;
    }
    public int compareTo(EData compare) {


        //ascending order
        if(this.rank- compare.rank >=0)
        {
            return 1;
        }
        return -1;
    }
}
