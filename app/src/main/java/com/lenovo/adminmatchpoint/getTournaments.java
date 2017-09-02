package com.lenovo.adminmatchpoint;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;

/**
 * displays tournaments whose registrtion deadline is finished so that admin can fill up the necessary info
 */
public class getTournaments extends AppCompatActivity {
    private DatabaseReference rootref = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference tournamentdata = rootref.child("tournamentdata");
    private ArrayList<String> tournamentinfo;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_tournaments);
        getTournamentLists();
    }
    public void getTournamentLists() {
        tournamentinfo = new ArrayList<String>();
        Query query = tournamentdata.orderByChild("tournamentName");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    String temp;
                    String tournamentname = (String) messageSnapshot.child("tournamentName").getValue();
                    String cityname = (String) messageSnapshot.child("city").getValue();
                    String venuename = (String) messageSnapshot.child("venue").getValue();
                    long year = (Long) messageSnapshot.child("startingDate").child("year").getValue();
                    long month = (Long) messageSnapshot.child("startingDate").child("month").getValue();
                    long dayofmonth = (Long) messageSnapshot.child("startingDate").child("date").getValue();
                    Date startingDate = new GregorianCalendar((int) year + 1900, (int) month, (int) dayofmonth).getTime();
                    DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                    String startingDateString = df.format(startingDate);
                    long year2 = (Long) messageSnapshot.child("registrationDeadline").child("year").getValue();
                    long month2 = (Long) messageSnapshot.child("registrationDeadline").child("month").getValue();
                    long dayofmonth2 = (Long) messageSnapshot.child("registrationDeadline").child("date").getValue();
                    Date registrationDeadline = new GregorianCalendar((int) year2 + 1900, (int) month2, (int) dayofmonth2).getTime();
                    Date d=new Date();
                    String registrationDeadlineString = df.format(registrationDeadline);
                    temp = tournamentname + "\n" + "Details\n" + "city:" + cityname + "\n" + "venuename" + venuename + "\n" + "Starting date:" + startingDateString + "\n";
                    temp = temp + "Registration Deadline:" + registrationDeadlineString;
                    if(d.after(registrationDeadline))//data will be filled for onlt those tournaments whose registration deadline is over
                        tournamentinfo.add(temp);
                }
                addbuttons();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void addbuttons() {
        Button my[] = new Button[tournamentinfo.size()];
        String st;
        for (int i = 0; i < tournamentinfo.size(); ++i) {
            my[i] = new Button(this);
            my[i].setText(tournamentinfo.get(i));
            my[i].setBackgroundColor(Color.parseColor("#E8F5E9"));
            my[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonpressed(v);
                }
            });
            LinearLayout ll = (LinearLayout) findViewById(R.id.tournamentlist);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(8, 8, 8, 8);
            ll.addView(my[i], lp);
        }
    }

    public void buttonpressed(View view) {
        Button b = (Button) view;
        String buttonText = b.getText().toString();
        Scanner sc = new Scanner(buttonText);
        String tname = sc.nextLine();
        intent=new Intent(this,displayClasses.class);
        intent.putExtra("TOURNAMENTID",tname);
        startActivity(intent);
    }
}
