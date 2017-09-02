package com.lenovo.adminmatchpoint;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import static android.R.attr.data;
import static android.R.attr.name;
import static android.R.attr.password;

/**
 * Enables the admin to create tournament
 */
public class AddTournamentActivity extends AppCompatActivity {
    private DatabaseReference rootref = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference tournamentdata = rootref.child("tournamentdata");
    private EditText tournamentnameetext, citytext, venuetext;
    private DatePicker sddatepicker, rddatepicker;
    private RadioGroup organizerradiogroup;
    private CategoryTypes ct;
    private TournamentData tournament;
    private RadioButton b11rb, g11rb, b13rb, g13rb, b15rb, g15rb, b17rb, g17rb, b19rb, g19rb;
    String tournamentname, city, venue, organizetype;
    Date sddate, rddate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tournament);
    }

    public void addNewTournament(View v) {
        tournamentnameetext = (EditText) findViewById(R.id.tournamentnametext);
        citytext = (EditText) findViewById(R.id.citytext);
        venuetext = (EditText) findViewById(R.id.venuetextt);
        sddatepicker = (DatePicker) findViewById(R.id.startingdate);
        rddatepicker = (DatePicker) findViewById(R.id.registrationdeadline);
        organizerradiogroup = (RadioGroup) findViewById(R.id.organizerrradiogroup);
        tournamentname = tournamentnameetext.getText().toString();
        city = citytext.getText().toString();
        venue = venuetext.getText().toString();
        sddate = new GregorianCalendar(sddatepicker.getYear(), sddatepicker.getMonth(), sddatepicker.getDayOfMonth()).getTime();
        rddate = new GregorianCalendar(rddatepicker.getYear(), rddatepicker.getMonth(), rddatepicker.getDayOfMonth()).getTime();

        organizetype = "";
        if (organizerradiogroup.getCheckedRadioButtonId() == R.id.matchpointradiobutton) {
            organizetype = "matchpoint";
        } else if (organizerradiogroup.getCheckedRadioButtonId() == R.id.externalradiobutton) {
            organizetype = "external";
        }
        ct = returnCategoryType();

        Query query = tournamentdata.orderByChild("tournamentname").equalTo(tournamentname);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() < 1) {
                    tournament = new TournamentData(tournamentname, city, venue, sddate, rddate, organizetype, ct);
                    String userId = tournamentdata.push().getKey();
                    tournamentdata.child(userId).setValue(tournament);
                    tournamentdata.child(userId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            Toast.makeText(AddTournamentActivity.this, "Tournament added successfully", Toast.LENGTH_LONG).show();

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(AddTournamentActivity.this, "Data could not be entered check connection", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Toast.makeText(AddTournamentActivity.this, "Id already taken try another", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    public CategoryTypes returnCategoryType() {
        b11rb = (RadioButton) findViewById(R.id.BU11radiobutton);
        b11rb = (RadioButton) findViewById(R.id.BU11radiobutton);
        b13rb = (RadioButton) findViewById(R.id.BU13radiobutton);
        b15rb = (RadioButton) findViewById(R.id.BU15radiobutton);
        b17rb = (RadioButton) findViewById(R.id.BU17radiobutton);
        b19rb = (RadioButton) findViewById(R.id.BU19radiobutton);
        g11rb = (RadioButton) findViewById(R.id.GU11radiobutton);
        g13rb = (RadioButton) findViewById(R.id.GU13radiobutton);
        g15rb = (RadioButton) findViewById(R.id.GU15radiobutton);
        g17rb = (RadioButton) findViewById(R.id.GU17radiobutton);
        g19rb = (RadioButton) findViewById(R.id.GU19radiobutton);
        CategoryTypes cats = new CategoryTypes();
        if (b11rb.isChecked()) {
            cats.BU11 = true;
        }
        if (b13rb.isChecked()) {
            cats.BU13 = true;
        }
        if (b15rb.isChecked()) {
            cats.BU15 = true;
        }
        if (b17rb.isChecked()) {
            cats.BU17 = true;
        }
        if (b19rb.isChecked()) {
            cats.BU19 = true;
        }
        if (g11rb.isChecked()) {
            cats.GU11 = true;
        }
        if (g13rb.isChecked()) {
            cats.GU13 = true;
        }
        if (g15rb.isChecked()) {
            cats.GU15 = true;
        }
        if (g17rb.isChecked()) {
            cats.GU17 = true;
        }
        if (g19rb.isChecked()) {
            cats.GU19 = true;
        }
        return cats;
    }
}
