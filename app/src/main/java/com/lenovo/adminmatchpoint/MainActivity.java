package com.lenovo.adminmatchpoint;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.attr.key;
import static android.R.attr.name;
import static android.R.attr.password;
import static android.R.attr.top;

/**
 * displays most of the functionality by admin matchpoint
 * @author atharva vyas
 */
public class MainActivity extends AppCompatActivity {
    private DatabaseReference rootref = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference playerpdata = rootref.child("playerpdata");
    private DatabaseReference playertdata = rootref.child("playertdata");
    private DatabaseReference monthrankdata=rootref.child("monthlyRankList");
    private String playerid, matchresult, cat, pid, gender;
    int agen;
    private long matchScore, count;
    private EditText pidtext, matchscoretext;
    private RadioGroup matchstatus;
    private ArrayList<IndividualMatchPoint> topPoints;
    /**
     * this hash map stores playerid as key and playercategory as value
     */
    private Map<String, String> map;
    private int countr=0;
    private String categoryList[]={"BU11","BU13","BU17","BU15","BU19","GU11","GU13","GU17","GU15","GU19"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * This method was used for testing .Method enables the admin to add score for a player it is not needed any more
     * @param view
     */
    public void enterScore(View view) {
        pidtext = (EditText) findViewById(R.id.playeridtext);
        matchscoretext = (EditText) findViewById(R.id.matchscoretext);
        matchstatus = (RadioGroup) findViewById(R.id.matchstatusradiogroup);//to find if player won or lost
        playerid = pidtext.getText().toString();
        matchScore = Long.parseLong(matchscoretext.getText().toString());
        if (matchstatus.getCheckedRadioButtonId() == R.id.winradiobutton) {
            matchresult = "won";
        } else if (matchstatus.getCheckedRadioButtonId() == R.id.lossradiobutton) {
            matchresult = "lost";
        }

        Query query = playertdata.orderByChild("playerid").equalTo(playerid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                        String key = messageSnapshot.getKey();
                        long totalpoints = (Long) messageSnapshot.child("totalPoints").getValue();
                        long tpointscount = (Long) messageSnapshot.child("topPointscount").getValue();
                        long yMatchesWon = (Long) messageSnapshot.child("yearlyMatchesWon").getValue();
                        long yMatchesLost = (Long) messageSnapshot.child("yearlyMatchesLost").getValue();
                        long cWin = (Long) messageSnapshot.child("careerWin").getValue();
                        long cLost = (Long) messageSnapshot.child("careerLoss").getValue();
                        String categ = (String) messageSnapshot.child("playercategory").getValue();
                        GenericTypeIndicator<ArrayList<IndividualMatchPoint>> gen = new GenericTypeIndicator<ArrayList<IndividualMatchPoint>>() {
                        };
                        topPoints = (ArrayList<IndividualMatchPoint>) messageSnapshot.child("toppoints").getValue(gen);
                        double averagePoints = (double) messageSnapshot.child("averagePoints").getValue();

                        if (tpointscount < 8) {
                            topPoints.add(new IndividualMatchPoint(matchScore, categ));
                            ++tpointscount;
                            totalpoints += matchScore;
                        } else {
                            long m = 10000, m2 = 10000;
                            boolean othercategory = false;
                            int mi = 0, mi2 = 0;
                            for (int i = 0; i < topPoints.size(); ++i) {
                                if (topPoints.get(i).matchScore != -1 && topPoints.get(i).matchScore < m) {
                                    m = topPoints.get(i).matchScore;
                                    mi = i;
                                }
                                if (topPoints.get(i).matchScore != -1 && topPoints.get(i).matchScore < m2 && !(topPoints.get(i).category.equals(categ))) {
                                    othercategory = true;
                                    mi2 = i;
                                    m2 = topPoints.get(i).matchScore;
                                }
                            }
                            if (othercategory == true) {
                                topPoints.set(mi2, new IndividualMatchPoint(matchScore, categ));
                            } else if (topPoints.get(mi).matchScore < matchScore) {
                                topPoints.set(mi, new IndividualMatchPoint(matchScore, categ));
                            }
                        }
                        totalpoints = 0;
                        for (int i = 1; i < topPoints.size(); ++i) {
                            if (topPoints.get(i).category.equals(categ)) {
                                totalpoints += topPoints.get(i).matchScore;
                            } else {
                                totalpoints += (topPoints.get(i).matchScore) / 2;
                            }
                        }
                        averagePoints = -(double) totalpoints / 8 - 0.005;
                        if (matchresult == "won") {
                            ++cWin;
                            ++yMatchesWon;
                        } else if (matchresult == "lost") {
                            ++cLost;
                            ++yMatchesLost;
                        }
                        playertdata.child(key).child("totalPoints").setValue(totalpoints);
                        playertdata.child(key).child("yearlyMatchesWon").setValue(yMatchesWon);
                        playertdata.child(key).child("yearlyMatchesLost").setValue(yMatchesLost);
                        playertdata.child(key).child("careerWin").setValue(cWin);
                        playertdata.child(key).child("careerLoss").setValue(cLost);
                        playertdata.child(key).child("toppoints").setValue(topPoints);
                        playertdata.child(key).child("topPointscount").setValue(tpointscount);
                        playertdata.child(key).child("averagePoints").setValue(averagePoints);
                        Toast.makeText(MainActivity.this, "values updated", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(MainActivity.this, "playerid not found check carefully", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    /**
     * using recursion to update ranks for each category ,there are total of 10 categories
     * @param v
     */
    public void updateRank(final View v) {
        if(countr==10)
        {
            countr=0;
            Toast.makeText(MainActivity.this, "ranks updated", Toast.LENGTH_LONG).show();
            return;
        }
        count=0;
        Query query = playertdata.orderByChild("averagePoints");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    String key = messageSnapshot.getKey();
                    if(((String)messageSnapshot.child("playercategory").getValue()).equals(categoryList[countr])) {
                        ++count;
                        playertdata.child(key).child("rank").setValue(count);
                    }
                }
                ++countr;
                updateRank(v);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     * this method updates the age of player if there needs to be and changes the player category accordingly
     * @param view
     */
    public void updateAge(View view) {
        map = new HashMap<String, String>();
        Query query = playerpdata.orderByChild("playerid");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    long year = (Long) messageSnapshot.child("dob").child("year").getValue();
                    long month = (Long) messageSnapshot.child("dob").child("month").getValue();
                    long dayofmonth = (Long) messageSnapshot.child("dob").child("date").getValue();
                    Date date = new GregorianCalendar((int) year + 1900, (int) month, (int) dayofmonth).getTime();
                    long agep = (Long) messageSnapshot.child("age").getValue();
                    gender = (String) messageSnapshot.child("gender").getValue();
                    pid = (String) messageSnapshot.child("playerid").getValue();
                    String key;
                    agen = getAge(date);
                    cat = getCategory(agen, gender);
                    key = messageSnapshot.getKey();
                    playerpdata.child(key).child("age").setValue(agen);
                    playerpdata.child(key).child("playercategory").setValue(cat);
                    map.put(pid, cat);

                }
                Query q2 = playertdata.orderByChild("playerid");
                q2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                            String key2 = messageSnapshot.getKey();
                            String plyerid = (String) messageSnapshot.child("playerid").getValue();
                            playertdata.child(key2).child("playercategory").setValue(map.get(plyerid));
                        }
                        updateTotal();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    /**
     * updates total points from top 8 match points as per their category
     */
    public void updateTotal() {
        Query query = playertdata.orderByChild("playerid");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                        String key = messageSnapshot.getKey();
                        long totalpoints = (Long) messageSnapshot.child("totalPoints").getValue();
                        String categ = (String) messageSnapshot.child("playercategory").getValue();
                        double averagePoints;
                        GenericTypeIndicator<ArrayList<IndividualMatchPoint>> gen = new GenericTypeIndicator<ArrayList<IndividualMatchPoint>>() {
                        };
                        topPoints = (ArrayList<IndividualMatchPoint>) messageSnapshot.child("toppoints").getValue(gen);
                        totalpoints = 0;
                        for (int i = 1; i < topPoints.size(); ++i) {
                            if (topPoints.get(i).category.equals(categ)) {
                                totalpoints += topPoints.get(i).matchScore;
                            } else {
                                totalpoints += (topPoints.get(i).matchScore) / 2;
                            }
                        }
                        averagePoints = -(double) totalpoints / 8 - 0.005; // 0.005 is used here so that average points remain double esle it can be termed as long in the firebase after entry
                        playertdata.child(key).child("totalPoints").setValue(totalpoints);
                        playertdata.child(key).child("averagePoints").setValue(averagePoints);
                        Toast.makeText(MainActivity.this, "values updated", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(MainActivity.this, "playerid not found check carefully", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     *
     * @param dateOfBirth dob in Date data type of java
     * @return gets age from date of birth entered
     */
    public static int getAge(Date dateOfBirth) {

        Calendar today = Calendar.getInstance();
        Calendar birthDate = Calendar.getInstance();

        int age = 0;

        birthDate.setTime(dateOfBirth);
        if (birthDate.after(today)) {
            throw new IllegalArgumentException("Can't be born in the future");
        }

        age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);

        // If birth date is greater than todays date (after 2 days adjustment of leap year) then decrement age one year
        if ((birthDate.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR) > 3) ||
                (birthDate.get(Calendar.MONTH) > today.get(Calendar.MONTH))) {
            age--;

            // If birth date and todays date are of same month and birth day of month is greater than todays day of month then decrement age
        } else if ((birthDate.get(Calendar.MONTH) == today.get(Calendar.MONTH)) &&
                (birthDate.get(Calendar.DAY_OF_MONTH) > today.get(Calendar.DAY_OF_MONTH))) {
            age--;
        }

        return age;
    }

    /**
     *
     * @param age
     * @param gender
     * @return category from age and gender
     */
    public static String getCategory(int age, String gender) {
        String category = "";
        if (gender.equals("male")) {
            category += "BU";
        } else {
            category += "GU";
        }
        if (age < 11) {
            category += "11";
        } else if (age < 13) {
            category += "13";
        } else if (age < 15) {
            category += "15";
        } else if (age < 17) {
            category += "17";
        } else if (age < 19) {
            category += "19";
        }
        return category;
    }

    /**
     * Enables the admin to create a new tournament
     * @param v
     */
    public void addTournament(View v) {
        Intent i = new Intent(this, AddTournamentActivity.class);
        startActivity(i);
    }

    /**
     * redirects to screen where tournaments whose registration deadline is over can be seen admin then enters tournament result
     * @param v
     */
    public void getTournament(View v)
    {
        Intent i = new Intent(this, getTournaments.class);
        startActivity(i);
    }

    /**
     * adds rank list of the month selected, useful for retrieving the rank of the month in which tournament was played
     * @param v
     */
    public void addRankList(View v)
    {
        final Map<String,MonthlyRankData> rmap=new HashMap<String, MonthlyRankData>();
        Query query = playertdata.orderByChild("playerid");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    String playercategory=(String)messageSnapshot.child("playercategory").getValue();
                    String city=(String)messageSnapshot.child("city").getValue();
                    long rank=(Long)messageSnapshot.child("rank").getValue();
                    MonthlyRankData tm=new MonthlyRankData(rank,playercategory,city);
                    String playerid=(String)messageSnapshot.child("playerid").getValue();
                    rmap.put(playerid,tm);
                }
                Date d=new Date();
                RankListData rl=new RankListData(d.getMonth(),d.getYear(),rmap);
                String userId1 = monthrankdata.push().getKey();
                monthrankdata.child(userId1).setValue(rl);
                monthrankdata.child(userId1).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Toast.makeText(MainActivity.this, "Rank List has been entered into database", Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(MainActivity.this, "Data could not be entered check connection", Toast.LENGTH_LONG).show();
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void addCenterSlots(View v)
    {
        Intent i = new Intent(this, AddCenterSlot.class);
        startActivity(i);
    }
}
