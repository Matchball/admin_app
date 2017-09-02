package com.lenovo.adminmatchpoint;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by atharva vyas on 18-06-2017.
 * Class contains information regarding particular tournament
 */

public class TournamentData {
    public String tournamentName;
    public String city;
    public String venue;
    public Date startingDate;
    public Date registrationDeadline;
    public String organizerType;
    public CategoryTypes cTypes;
    public ArrayList<MyClass> enrolled;

    public TournamentData() {

    }

    public TournamentData(String tname, String c, String v, Date sd, Date rd, String ot, CategoryTypes ct) {
        this.tournamentName = tname;
        this.city = c;
        this.venue = v;
        this.startingDate = sd;
        this.registrationDeadline = rd;
        this.organizerType = ot;
        this.cTypes = ct;
        this.enrolled = new ArrayList<MyClass>();
        this.enrolled.add(new MyClass("dummy","dummyclass"));
    }
}
