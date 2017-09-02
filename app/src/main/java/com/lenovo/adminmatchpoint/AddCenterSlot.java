package com.lenovo.adminmatchpoint;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.NumberPicker;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.GregorianCalendar;
/*Add center and available slots that will be used to validate the registration process of player*/
public class AddCenterSlot extends AppCompatActivity {
    private DatabaseReference rootref = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference Center = rootref.child("Center");

    private EditText center;
    private DatePicker slot;
    private TextView tv;
    private EditText np;
    private CenterSlot center1;
    String centername;
    Date slot_avail;
    String slot_avil2;
    String accesstype;
    private RadioButton R1;
    private RadioGroup group;
    long capacity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_center_slot);
    }
    public void Submit(View v)
    {

        center = (EditText) findViewById(R.id.Center);
        DatabaseReference center_name=Center.child(center.getText().toString());
        slot=(DatePicker) findViewById(R.id.Slot);
        group = (RadioGroup) findViewById(R.id.radioaccess);
       tv = (TextView) findViewById(R.id.tv);
        np=(EditText) findViewById(R.id.np);

        /*np = (NumberPicker) findViewById(R.id.np);
        //Set the minimum value of NumberPicker
        np.setMinValue(0);
        //Specify the maximum value/number of NumberPicker
        np.setMaxValue(25);
        //Gets whether the selector wheel wraps when reaching the min/max value.
        np.setWrapSelectorWheel(false);

        //Set a value change listener for NumberPicker
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                //Display the newly selected number from picker
                tv.setText("Selected Number : " + newVal);
                capacity=newVal;
            }
        });*/
        String value= np.getText().toString();
        capacity=Long.parseLong(value);
        centername = center.getText().toString();
        slot_avail = new GregorianCalendar(slot.getYear(), slot.getMonth()+1, slot.getDayOfMonth()).getTime();
        /*slot_avil2 = (String)slot.getDayOfMonth()+(String) slot.getMonth()+1+(String) slot.getYear();*/
        int day = slot.getDayOfMonth();
        int month = slot.getMonth()+1;
        int year = slot.getYear();

        slot_avil2 =  Integer.toString(day)+"/"+Integer.toString(month)+"/"+Integer.toString(year);

        int selected=group.getCheckedRadioButtonId();
        R1=(RadioButton) findViewById(selected);
        accesstype=R1.getText().toString();

        center_name.setValue(new CenterSlot(centername, slot_avil2,accesstype,capacity));
        Toast.makeText(getApplicationContext(), "Center and available slots added successfully", Toast.LENGTH_LONG).show();

       /* Query query = Center.orderByChild("centername").equalTo(centername);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() < 1) {
                     center1 = new CenterSlot(centername, slot_avil2,accesstype,capacity);
                    String data = Center.push().getKey();
                    Center.child(data).setValue(center1);
                    Center.child(data).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Toast.makeText(AddCenterSlot.this, "Center and available slots added successfully", Toast.LENGTH_LONG).show();

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(AddCenterSlot.this, "Data could not be entered check connection", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Toast.makeText(AddCenterSlot.this, "Id already taken try another", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });*/
    }


}
