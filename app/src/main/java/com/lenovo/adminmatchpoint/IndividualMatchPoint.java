package com.lenovo.adminmatchpoint;

/**
 * Created by atharva on 17-06-2017.
 */

public class IndividualMatchPoint {
    public long matchScore;
    public String category;

    public IndividualMatchPoint(long m, String c) {
        this.matchScore = m;
        this.category = c;
    }

    public IndividualMatchPoint() {

    }
}
