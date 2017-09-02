package com.lenovo.adminmatchpoint;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Deque;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.R.attr.button;
import static android.R.attr.id;
import static android.R.attr.key;
import static android.R.attr.linksClickable;
import static android.R.id.closeButton;
import static android.R.string.no;
import static android.media.CamcorderProfile.get;
import static com.lenovo.adminmatchpoint.MainActivity.getAge;

/**
 * This class gives us the tournament arrangement of a particular category i.e which player is going to play against whom
 * @author atharva vyas
 */
public class TournamentSchedule extends AppCompatActivity {
    private DatabaseReference rootref = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference playertdata = rootref.child("playertdata");
    private DatabaseReference tournamentdata = rootref.child("tournamentdata");
    private DatabaseReference tournamentmsdata = rootref.child("tournamentmsdata");
    /**
     * category for whic we will be showing match arrangements
     */
    private String categoryAnalysed;
    private Map<String, Long> map;
    /**
     * Set containing plaers that got bye
     */
    private Set<String> byeplayers;
    private String tname,p1,p2;
    private ArrayList<EData> ed;
    private ArrayList<MyType> finalScheme;
    private ArrayList<MyClass> enrolled;
    private ArrayList<String> enrolledn;
    private Deque<String> firstFirstHalf, secondFirstHalf, firstSecondHalf, secondSecondHalf;//players in the first half of the first half-firstFirstHalf
    private Vector<MyType> firstHalf, secondHalf;
    private long p, nob, nop;
    private float screenWidth, screenHeight;
    /**
     * maps the lace from one round to another round position the player who will win the round will occupy that position.
     * each match has unique integer id
     */
    private Map<Integer, Integer> linkage;
    private Map<String, Integer> pointallotment;
    /**
     * map between integer id to the match between two players that will be plaayed
     */
    private Map<Integer, Match> matchMap;
    private Map<Integer,TournamentMatch> matchScoreMap;
    private ArrayList<TournamentMatch> matchList;
    private int[] pointtable = {270, 180, 105, 60, 32, 16, 8};
    /**
     * pop up window to fill matchcores of a perticular match
     */
    private PopupWindow mPopupWindow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        matchScoreMap=new HashMap<Integer, TournamentMatch>();
        matchList=new ArrayList<TournamentMatch>();
        byeplayers=new HashSet<String>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament_schedule);
        getSchedule();
        screenWidth = getWindowManager()
                .getDefaultDisplay().getWidth();
        screenHeight = getWindowManager()
                .getDefaultDisplay().getHeight();
        categoryAnalysed=getIntent().getStringExtra("CATEGORY");
    }

    public void getSchedule() {
        map = new HashMap<String, Long>();//mapping from pplayer id to rank
        Query query = playertdata.orderByChild("rank");
        tname = getIntent().getStringExtra("TOURNAMENTID");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    long rank = (Long) messageSnapshot.child("rank").getValue();
                    ;
                    String pid = (String) messageSnapshot.child("playerid").getValue();
                    map.put(pid, rank);
                }
                Query q2 = tournamentdata.orderByChild("tournamentName").equalTo(tname);
                q2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                            String key2 = messageSnapshot.getKey();
                            String plyerid = (String) messageSnapshot.child("playerid").getValue();
                            playertdata.child(key2).child("playercategory").setValue(map.get(plyerid));
                            GenericTypeIndicator<ArrayList<MyClass>> gen = new GenericTypeIndicator<ArrayList<MyClass>>() {
                            };
                            enrolled = (ArrayList<MyClass>) messageSnapshot.child("enrolled").getValue(gen);//contains p,ayers enrooled for tournament
                            enrolledn=new ArrayList<String>();
                            ed = new ArrayList<EData>();
                            EData temp;
                            //Toast.makeText(TournamentSchedule.this, "categoryAnalysed:"+categoryAnalysed, Toast.LENGTH_LONG).show();
                            for (int i = 1; i < enrolled.size(); ++i) {
                                //Toast.makeText(TournamentSchedule.this, "enrolled.get(i).category"+enrolled.get(i).category, Toast.LENGTH_LONG).show();
                                if((enrolled.get(i).category).equals(categoryAnalysed)) {
                                    temp = new EData(enrolled.get(i).playername, map.get(enrolled.get(i).playername));
                                    enrolledn.add(enrolled.get(i).playername);
                                    ed.add(temp);
                                }
                            }
                            if(ed.size()==0)
                            {
                                Toast.makeText(TournamentSchedule.this, "zero players for this category", Toast.LENGTH_LONG).show();
                                return;
                            }
                            Collections.sort(ed);
                            firstFirstHalf = new LinkedList<String>();
                            secondSecondHalf = new LinkedList<String>();
                            firstSecondHalf = new LinkedList<String>();
                            secondFirstHalf = new LinkedList<String>();
                            nop = ed.size();
                            TextView nopt = ((TextView) findViewById(R.id.nop));
                            nopt.setText("number of players:" + nop);

                            for (int i = 0; i < ed.size(); ++i) {
                                if (i % 4 == 0) {
                                    firstFirstHalf.addLast(ed.get(i).playerid);
                                } else if (i % 4 == 1) {
                                    secondSecondHalf.addFirst(ed.get(i).playerid);
                                } else if (i % 4 == 2) {
                                    secondFirstHalf.addLast(ed.get(i).playerid);
                                } else if (i % 4 == 3) {
                                    firstSecondHalf.addFirst(ed.get(i).playerid);
                                }
                            }
                            firstHalf = new Vector<MyType>();
                            secondHalf = new Vector<MyType>();
                            MyType temp2;
                            for (String pid : firstFirstHalf) {

                                temp2 = new MyType();
                                temp2.byestatus = false;
                                temp2.playerid = pid;
                                firstHalf.add(temp2);
                            }
                            for (String pid : firstSecondHalf) {

                                temp2 = new MyType();
                                temp2.byestatus = false;
                                temp2.playerid = pid;
                                firstHalf.add(temp2);
                            }
                            for (String pid : secondFirstHalf) {

                                temp2 = new MyType();
                                temp2.byestatus = false;
                                temp2.playerid = pid;
                                secondHalf.add(temp2);
                            }
                            for (String pid : secondSecondHalf) {

                                temp2 = new MyType();
                                temp2.byestatus = false;
                                temp2.playerid = pid;
                                secondHalf.add(temp2);
                            }
                            for (int i = 1; i < 20; ++i) {
                                if (Math.pow(2, i) - ed.size() > -0.1) {
                                    p = i;
                                    break;
                                }
                            }

                            nob = ((long) Math.pow(2, p)) - nop;
                            ((TextView) findViewById(R.id.nob)).setText("number of byes:" + nob);
                            long count = nob / 4;

                            if (nob % 4 - 1 >= 0) {

                                for (int i = 0; i < nob / 4 + 1; ++i) {
                                    firstHalf.get(i).byestatus = true;
                                }
                            } else {
                                for (int i = 0; i < nob / 4; ++i) {
                                    firstHalf.get(i).byestatus = true;
                                }
                            }
                            if (nob % 4 - 2 >= 0) {
                                for (int i = secondHalf.size() - 1; i > secondHalf.size() - 1 - (nob / 4 + 1); --i) {
                                    secondHalf.get(i).byestatus = true;
                                }
                            } else {
                                for (int i = secondHalf.size() - 1; i > secondHalf.size() - 1 - (nob / 4); --i) {
                                    secondHalf.get(i).byestatus = true;
                                }
                            }
                            if (nob % 4 - 3 >= 0) {
                                for (int i = 0; i < nob / 4 + 1; ++i) {
                                    secondHalf.get(i).byestatus = true;
                                }
                            } else {
                                for (int i = 0; i < nob / 4; ++i) {
                                    secondHalf.get(i).byestatus = true;
                                }
                            }
                            for (int i = firstHalf.size() - 1; i > firstHalf.size() - 1 - (nob / 4); --i) {
                                firstHalf.get(i).byestatus = true;
                            }
                        }

                        //addbuttons1();
                        //addbuttons2();
                        addLayouts();
                        //updateTotal();
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
     * after the arrangement of players is done creating latout for the whole tournament
     */
    public void addLayouts() {
        linkage = new HashMap<Integer, Integer>();
        matchMap=new HashMap<Integer, Match>();
        /*
        for a particluar match of round no.i id is 100*i + count
        p is the number of rounds creating layouts for each particular round
         */
        LinearLayout lls[] = new LinearLayout[(int) p + 1];
        for (int i = 0; i < p + 1; ++i) {
            lls[i] = new LinearLayout(this);
            lls[i].setBackgroundColor(Color.parseColor("#E8A5E9"));
            LinearLayout ll = (LinearLayout) findViewById(R.id.allplayers);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int) screenWidth / ((int) p + 1),
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            lp.setMargins(8, 8, 8, 8);
            ll.addView(lls[i], lp);
            lls[i].setOrientation(LinearLayout.VERTICAL);
            int count = 0, countl = 0;
            if (i == 0) {
                Button my[] = new Button[firstHalf.size()];
                String st;
                for (int j = 0; j < firstHalf.size(); ++j) {
                    my[j] = new Button(this);
                    if (firstHalf.get(j).byestatus)
                        my[j].setText(firstHalf.get(j).playerid + "\n" + "bye");
                    else my[j].setText(firstHalf.get(j).playerid + "\n");
                    if (!linkage.containsKey(100 * i + count)) {
                        if (firstHalf.get(j).byestatus) {
                            linkage.put(100 * i + count, 100 * (i + 1) + countl);
                            matchMap.put(100 * (i + 1) + countl, new Match("bye"));
                            ++countl;
                        } else {
                            linkage.put(100 * i + count, 100 * (i + 1) + countl);
                            linkage.put(100 * i + count + 1, 100 * (i + 1) + countl);
                            matchMap.put(100 * (i + 1) + countl, new Match(100 * i + count, 100 * i + count + 1, "match"));
                            ++countl;
                        }
                    }
                    my[j].setBackgroundColor(Color.parseColor("#E8F5E9"));
                    my[j].setId(100 * i + count);
                    ++count;
                    my[j].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v1) {
                             playerButtonFunction(v1);
                        }
                    });
                    LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp1.setMargins(8, 8, 8, 8);
                    lls[i].addView(my[j], lp1);
                }
                my = new Button[secondHalf.size()];
                for (int j = 0; j < secondHalf.size(); ++j) {
                    my[j] = new Button(this);
                    if (secondHalf.get(j).byestatus)
                        my[j].setText(secondHalf.get(j).playerid + "\n" + "bye");
                    else my[j].setText(secondHalf.get(j).playerid + "\n");
                    if (!linkage.containsKey(100 * i + count)) {
                        if (secondHalf.get(j).byestatus) {
                            linkage.put(100 * i + count, 100 * (i + 1) + countl);
                            matchMap.put(100 * (i + 1) + countl, new Match("bye"));
                            ++countl;
                        } else {
                            linkage.put(100 * i + count, 100 * (i + 1) + countl);
                            linkage.put(100 * i + count + 1, 100 * (i + 1) + countl);
                            matchMap.put(100 * (i + 1) + countl, new Match(100 * i + count, 100 * i + count + 1, "match"));
                            ++countl;
                        }
                    }
                    my[j].setBackgroundColor(Color.parseColor("#E8A5E9"));
                    my[j].setId(100 * i + count);
                    ++count;
                    my[j].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v1) {
                            playerButtonFunction(v1);
                        }
                    });
                    LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp2.setMargins(8, 8, 8, 8);
                    lls[i].addView(my[j], lp2);
                }
            } else {
                Button my[] = new Button[(int) Math.pow(2, p - i)];
                String st;
                for (int j = 0; j < (int) Math.pow(2, p - i); ++j) {
                    my[j] = new Button(this);
                    my[j].setText("winner");
                    my[j].setBackgroundColor(Color.parseColor("#E8F5E9"));
                    my[j].setId(100 * i + j);
                    if (i != p) {
                        linkage.put(100 * i + j, 100 * (i + 1) + j / 2);
                        matchMap.put(100 * (i + 1) + j / 2, new Match(100 * i + (j/2)*2, 100 * i + (j/2)*2 + 1, "match"));
                    }
                    my[j].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v1) {
                            playerButtonFunction(v1);
                        }
                    });
                    LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp1.setMargins(8, 8, 8, 8);
                    lls[i].addView(my[j], lp1);
                }
            }
        }
    }

    /**
     * pop up window functionality after clicking on a partcular match
     * @param v1
     */
    public void playerButtonFunction(final View v1)
    {
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.custom_layout, null);

        mPopupWindow = new PopupWindow(
                customView,
                LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT
        );
        mPopupWindow.setFocusable(true);
        mPopupWindow.update();
        if (Build.VERSION.SDK_INT >= 21) {
            mPopupWindow.setElevation(5.0f);
        }
        Button bi = (Button) v1;
        int bid = bi.getId();
        int tid = linkage.get(bid);
        String mt = matchMap.get(tid).matchType;
        final TextView matchdisplayer = (TextView) customView.findViewById(R.id.matchdisplayer);
        final EditText matchscores=(EditText) customView.findViewById(R.id.edittextpopup);
        final RadioButton mt3=(RadioButton) customView.findViewById(R.id.threematch);
        final RadioButton mt5=(RadioButton) customView.findViewById(R.id.fivematch);
        if (mt.equals("bye")) {
            matchdisplayer.setText("bye");
            Button b = (Button) v1;
            String buttonText = b.getText().toString();
            Scanner sc = new Scanner(buttonText);
            String playername = sc.nextLine();
            byeplayers.add(playername);
            matchscores.setVisibility(View.INVISIBLE);
            mt3.setVisibility(View.INVISIBLE);
            mt5.setVisibility(View.INVISIBLE);
        } else {
            int pid1 = matchMap.get(tid).player1id;
            int pid2 = matchMap.get(tid).player2id;
            String buttonText = ((Button) findViewById(pid1)).getText().toString();
            Scanner sc = new Scanner(buttonText);
            p1 = sc.nextLine();
            buttonText = ((Button) findViewById(pid2)).getText().toString();
            sc = new Scanner(buttonText);
            p2 = sc.nextLine();
            matchdisplayer.setText(p1 + " vs " + p2);
        }
        // Get a reference for the custom view cancel button
        Button closeButton = (Button) customView.findViewById(R.id.cancelbutton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
            }
        });
        Button enterbutton = (Button) customView.findViewById(R.id.enterbutton);
        enterbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!matchdisplayer.getText().equals("bye")) {
                    if(mt3.isChecked()) {
                        String winner = returnWinner(p1, p2, matchscores.getText().toString(), 3);
                        buttonpressed(v1, winner,p1,p2,matchscores.getText().toString(),"three match");
                    }
                    else if(mt5.isChecked())
                    {
                        String winner = returnWinner(p1, p2, matchscores.getText().toString(), 5);
                        buttonpressed(v1, winner,p1,p2,matchscores.getText().toString(),"five match");
                    }
                    else
                    {
                        Toast.makeText(TournamentSchedule.this, "Select atleast one match type", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    buttonpressed(v1);
                }
                mPopupWindow.dismiss();
            }
        });
        mPopupWindow.showAtLocation((LinearLayout) findViewById(R.id.tournamentschedule), Gravity.CENTER, 0, 0);
    }

    /**
     *
     * @param p1 player 1
     * @param p2 player 2
     * @param matchScores match score data
     * @param mt match type whether best of five or best of three
     * @return winner of the match
     */
    public String returnWinner(String p1,String p2,String matchScores,int mt)
    {
        Pattern pattern = Pattern.compile("(\\d+)"); //to get seperate integers from string
        Matcher matcher = pattern.matcher(matchScores);
        List<Integer> numbers = new LinkedList<Integer>();
        while (matcher.find()) {
            numbers.add(Integer.valueOf(matcher.group(1)));
        }
        Integer[] output = numbers.toArray(new Integer[numbers.size()]);
        int l=output.length;// total numer of integer scores
        int p1count=0,p2count=0,count=0;// wins of player1 or player2
        if(mt==3)
        {
                int p1scores[] = new int[l/2];
                int p2scores[] = new int[l/2];
                for (int i = 0; i < l/2; ++i) {
                    p1scores[i] = output[count];
                    ++count;
                    p2scores[i] = output[count];
                    ++count;
                }
                for (int i = 0; i < l/2; ++i) {
                    if (p1scores[i] > p2scores[i]) {
                        if(p1scores[i]>=11 && p1scores[i]-p2scores[i]>=2)
                        ++p1count;
                        else
                        {
                            Toast.makeText(TournamentSchedule.this, "At least one player should reach 11 points and score difference >= 2 points.", Toast.LENGTH_LONG).show();
                            return "winner";
                        }
                    } else {
                        if(p2scores[i]>=11 && p2scores[i]-p1scores[i]>=2)
                            ++p2count;
                        else
                        {
                            Toast.makeText(TournamentSchedule.this, "At least one player should reach 11 points and score difference >= 2 points.", Toast.LENGTH_LONG).show();
                            return "winner";
                        }
                    }
                }
                if (p1count > p2count && p1count>=2) {
                    return p1;
                } else if(p2count > p1count && p2count>=2)
                    return p2;
                else {
                    Toast.makeText(TournamentSchedule.this, "Data entered improper check again", Toast.LENGTH_LONG).show();
                    return "winner";
                }

        }
        else
        {
                int p1scores[] = new int[l/2];
                int p2scores[] = new int[l/2];
                for (int i = 0; i < l/2; ++i) {
                    p1scores[i] = output[count];
                    ++count;
                    p2scores[i] = output[count];
                    ++count;
                }
                for (int i = 0; i < l/2; ++i) {
                    if (p1scores[i] > p2scores[i]) {
                        if(p1scores[i]>=11 && p1scores[i]-p2scores[i]>=2)
                            ++p1count;
                        else
                        {
                            Toast.makeText(TournamentSchedule.this, "At least one player should reach 11 points and score difference >= 2 points.", Toast.LENGTH_LONG).show();
                            return "winner";
                        }
                    } else {
                        if(p2scores[i]>=11 && p2scores[i]-p1scores[i]>=2)
                            ++p2count;
                        else
                        {
                            Toast.makeText(TournamentSchedule.this, "At least one player should reach 11 points and score difference >= 2 points.", Toast.LENGTH_LONG).show();
                            return "winner";
                        }
                    }
                }
                if (p1count > p2count && p1count>=3) {
                    return p1;
                } else if(p2count > p1count && p2count>=3)
                    return p2;
                else {
                    Toast.makeText(TournamentSchedule.this, "Data entered improper check again", Toast.LENGTH_LONG).show();
                    return "winner";
                }

        }
    }

    /**
     * functionality to be perfomed after data is properly entered in pop up window and match is not bye
     * @param v
     * @param winner
     * @param player1
     * @param player2
     * @param ms - matchscores
     * @param mt - match type best of 3 or 5
     */
    public void buttonpressed(View v,String winner,String player1,String player2,String ms,String mt)
    {
        Button b = (Button) v;
        String buttonText = b.getText().toString();
        Scanner sc = new Scanner(buttonText);
        int buttonid = b.getId();
        if (linkage.containsKey(buttonid)) {
            int targetid = linkage.get(buttonid);
            ((Button) findViewById(targetid)).setText(winner);
            TournamentMatch temp=new TournamentMatch(player1,player2,ms,winner,mt,targetid);
            matchScoreMap.put(targetid,temp);
        }
    }

    /**
     * functionality to be perfomed after data is properly entered in pop up window and match is  bye
     * @param view
     */
    public void buttonpressed(View view) {
        Button b = (Button) view;
        String buttonText = b.getText().toString();
        Scanner sc = new Scanner(buttonText);
        String playername = sc.nextLine();

        int buttonid = b.getId();
        if (linkage.containsKey(buttonid)) {
            int targetid = linkage.get(buttonid);
            //b.setText("tid " + targetid);
            ((Button) findViewById(targetid)).setText(playername);
            TournamentMatch temp=new TournamentMatch(playername,playername,"bye",playername,"bye",targetid);
            matchScoreMap.put(targetid,temp);
        }

    }

    /**
     * This method enters all the match scores of a particular tournament and category
     */
    public void enterMatchScores()
    {
        Iterator it = matchScoreMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            matchList.add((TournamentMatch)pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }
        TournamentMSData node=new TournamentMSData(tname,new Date(),matchList,enrolledn,pointallotment,p,categoryAnalysed);
        String key = tournamentmsdata.push().getKey();
        tournamentmsdata.child(key).setValue(node);
        tournamentmsdata.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Toast.makeText(TournamentSchedule.this, "Every score of match have been entered successfully", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(TournamentSchedule.this, "Data could not be entered check connection", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * assigns points as per the round in which they lost
     * @param v
     */
    public void assignPoints(View v) {
        pointallotment = new HashMap<String, Integer>();
        boolean b = true;// to check if all the match scores data has been properly filled
        for (int i = 0; i < p + 1; ++i) {
            int count = 0;
            if (i == 0) {
                for (int j = 0; j < firstHalf.size(); ++j) {
                    if (((Button) findViewById(100 * i + count)).getText().equals("winner"))
                        b = false;
                    ++count;
                }
                for (int j = 0; j < secondHalf.size(); ++j) {
                    if (((Button) findViewById(100 * i + count)).getText().equals("winner"))
                        b = false;
                    ++count;
                }
            } else {
                for (int j = 0; j < (int) Math.pow(2, p - i); ++j) {
                    if (((Button) findViewById(100 * i + j)).getText().equals("winner"))
                        b = false;
                }
            }
        }
        if (b == false) {
            Toast.makeText(TournamentSchedule.this, "entry of all match results not done", Toast.LENGTH_LONG).show();
        } else {
            for (int i = (int) p; i >= 0; --i) {
                if (i == 0) {
                    for (int j = firstHalf.size() + secondHalf.size() - 1; j >= 0; --j) {
                        String buttonText = ((Button) findViewById(100 * i + j)).getText().toString();
                        Scanner sc = new Scanner(buttonText);
                        String playername = sc.nextLine();
                        if (!pointallotment.containsKey(playername)) {
                            putMatchPoint(playername, pointtable[(int) p - i]);
                            pointallotment.put(playername, pointtable[(int) p - i]);
                        }
                    }
                } else {
                    for (int j = 0; j < (int) Math.pow(2, p - i); ++j) {
                        String playername = ((Button) findViewById(100 * i + j)).getText().toString();
                        if (!pointallotment.containsKey(playername)) {
                            putMatchPoint(playername, pointtable[(int) p - i]);
                            pointallotment.put(playername, pointtable[(int) p - i]);
                        }
                    }
                }
            }
            Toast.makeText(TournamentSchedule.this, "each player appointed proper points", Toast.LENGTH_LONG).show();
            enterMatchScores();
        }
    }

    /**
     * Enters match point in the player's tournament profile in database
     * @param playerid
     * @param points
     */
    public void putMatchPoint(final String playerid, final int points) {
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
                        ArrayList<IndividualMatchPoint> topPoints = (ArrayList<IndividualMatchPoint>) messageSnapshot.child("toppoints").getValue(gen);
                        double averagePoints = (double) messageSnapshot.child("averagePoints").getValue();
                        for(int x=0;x<=6;++x)
                        {
                            if(pointtable[x]==points)
                            {
                                if(x==0)
                                {
                                    //wins and loss depends on number of round and if got bye in the first round
                                    if(byeplayers.contains(playerid)) {
                                        cWin = cWin + p - x - 1;
                                        yMatchesWon = yMatchesWon + p - x-1;
                                    }
                                    else {
                                        cWin = cWin + p - x;
                                        yMatchesWon = yMatchesWon + p - x;
                                    }
                                }
                                else
                                {
                                    if(byeplayers.contains(playerid)) {
                                        cWin = cWin + p - x - 1;
                                        yMatchesWon = yMatchesWon + p - x;
                                    }
                                    else {
                                        cWin = cWin + p - x;
                                        yMatchesWon = yMatchesWon + p - x;
                                    }
                                    cLost=cLost+1;
                                    yMatchesLost=yMatchesLost+1;
                                }
                            }
                        }
                        if (tpointscount < 8) {
                            topPoints.add(new IndividualMatchPoint(points, categ));
                            ++tpointscount;
                            totalpoints += points;
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
                                topPoints.set(mi2, new IndividualMatchPoint(points, categ));
                            } else if (topPoints.get(mi).matchScore < points) {
                                topPoints.set(mi, new IndividualMatchPoint(points, categ));
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
                        /*if (matchresult == "won") {
                            ++cWin;
                            ++yMatchesWon;
                        } else if (matchresult == "lost") {
                            ++cLost;
                            ++yMatchesLost;
                        }*/
                        playertdata.child(key).child("totalPoints").setValue(totalpoints);
                        playertdata.child(key).child("yearlyMatchesWon").setValue(yMatchesWon);
                        playertdata.child(key).child("yearlyMatchesLost").setValue(yMatchesLost);
                        playertdata.child(key).child("careerWin").setValue(cWin);
                        playertdata.child(key).child("careerLoss").setValue(cLost);
                        playertdata.child(key).child("toppoints").setValue(topPoints);
                        playertdata.child(key).child("topPointscount").setValue(tpointscount);
                        playertdata.child(key).child("averagePoints").setValue(averagePoints);
                    }

                } else {
                    Toast.makeText(TournamentSchedule.this, "playerid not found check carefully", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}

class MyType {
    public String playerid;
    public boolean byestatus;
}
