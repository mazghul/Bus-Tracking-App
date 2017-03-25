// Manual Bus Searching Page.. We are not using it.. Its an added feature.
//Bus search results are taken from firebase cloud.

package com.vuedata.bustrackingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SearchBusActivity extends AppCompatActivity {
    private static final String TAG = SearchBusActivity.class.getName();
    EditText from, to;
    TextView details;
    String txtfrom, txtto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_bus);
        from = (EditText) findViewById(R.id.from);
        to = (EditText) findViewById(R.id.to);
        details = (TextView) findViewById(R.id.details);
    }

    public void search(View view) {
        /*if((email.getText().toString().equals("janani@plc.com") )&& (password.getText().toString().equals("Maziya"))){*/
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("first");
        DatabaseReference myRef1 = database.getReference("Bus");
        myRef.child("Starting/haha").setValue("Hello Maz!!");
        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.child("Starting/haha").getValue(String.class);
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        myRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
               /* String a= "Purasaiwalkam", b= "Besant Nagar";*/
                txtfrom = from.getText().toString().trim();
                txtto = to.getText().toString().trim();
                String BusNo = dataSnapshot.child(txtfrom + "/" + txtto + "/Bus No").getValue(String.class);
                String Name = dataSnapshot.child("Purasaiwalkam/Besant Nagar/Name").getValue(String.class);
                Log.d(TAG, "Bus is: " + BusNo);
                Log.d(TAG, "Bus Nme : " + Name);
                details.setText("Name is " + Name + " No is " + BusNo);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }


        });
    }

    public void track(View view) {
        Intent i = new Intent(this, TrackingActivity.class);
        startActivity(i);
    }

}
