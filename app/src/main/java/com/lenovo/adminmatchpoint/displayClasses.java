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

import java.util.ArrayList;
import java.util.Scanner;

import static android.R.attr.category;
import static android.R.attr.data;

/**
 * displays all the classes allowed by tournament so that the admin can chooose the tournament and fill up the match scores
 */
public class displayClasses extends AppCompatActivity {
    private DatabaseReference rootref = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference tournamentdata = rootref.child("tournamentdata");
    private String tname;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_classes);
        tname=getIntent().getStringExtra("TOURNAMENTID");
        executeQuery(tname);
    }

    /**
     * checks for all the categories that play in tournament and stores them in list
     * @param tname-tournament name
     */
    public void executeQuery(String tname)
    {
        Query query = tournamentdata.orderByChild("tournamentName").equalTo(tname);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {

                    GenericTypeIndicator<CategoryTypes> gen = new GenericTypeIndicator<CategoryTypes>() {
                    };
                    CategoryTypes categoryTypes = (CategoryTypes) messageSnapshot.child("cTypes").getValue(gen);
                    ArrayList<String> categoryList=getList(categoryTypes);
                    addbuttons(categoryList);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    public ArrayList<String> getList(CategoryTypes categoryTypes)
    {
        ArrayList<String> categoryList=new ArrayList<String>();
        if(categoryTypes.BU11==true)
        {
            categoryList.add("BU11");
        }
        if(categoryTypes.BU13==true)
        {
            categoryList.add("BU13");
        }
        if(categoryTypes.BU15==true)
        {
            categoryList.add("BU15");
        }
        if(categoryTypes.BU17==true)
        {
            categoryList.add("BU17");
        }
        if(categoryTypes.BU19==true)
        {
            categoryList.add("BU19");
        }
        if(categoryTypes.GU11==true)
        {
            categoryList.add("GU11");
        }
        if(categoryTypes.GU13==true)
        {
            categoryList.add("GU13");
        }
        if(categoryTypes.GU15==true)
        {
            categoryList.add("GU15");
        }
        if(categoryTypes.GU17==true)
        {
            categoryList.add("GU17");
        }
        if(categoryTypes.GU19==true)
        {
            categoryList.add("GU19");
        }
        return categoryList;
    }

    /**
     * opens up the player arrangements when the category is sselected
     * @param view
     */
    public void buttonpressed(View view) {
        Button b = (Button) view;
        String buttonText = b.getText().toString();
        Scanner sc = new Scanner(buttonText);
        String cat = sc.nextLine();
        intent=new Intent(this,TournamentSchedule.class);
        intent.putExtra("TOURNAMENTID",tname);
        intent.putExtra("CATEGORY",cat);
        startActivity(intent);
    }

    /**
     * adds categories allowed to the layout
     * @param categoryList-list of all the categories allowed
     */
    public void addbuttons(ArrayList<String> categoryList) {
        Button my[]=new Button[categoryList.size()];
        for (int i = 0; i < categoryList.size(); ++i) {
            my[i] = new Button(this);
            my[i].setText(categoryList.get(i));
            my[i].setBackgroundColor(Color.parseColor("#E8F5E9"));
            my[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonpressed(v);
                }
            });
            LinearLayout ll = (LinearLayout) findViewById(R.id.class_layout);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(8, 8, 8, 8);
            ll.addView(my[i], lp);

        }
    }
}



