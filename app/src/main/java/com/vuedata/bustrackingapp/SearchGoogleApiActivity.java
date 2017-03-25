// Bus search result is diplayed using the result from Google REST transit maps api. API google API is called and result is process.

package com.vuedata.bustrackingapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class SearchGoogleApiActivity extends AppCompatActivity implements LocationFinderListener {
    private static final String TAG = SearchGoogleApiActivity.class.getName();
    EditText from, to;
    TextView details;
    String origin, destination, bus;
    String dep, dep_time, bus_no;
    int j = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_google_api);

        // Getting front end values for processing
        from = (EditText) findViewById(R.id.fromapi);
        to = (EditText) findViewById(R.id.toapi);
        details = (TextView) findViewById(R.id.detailsapi);
    }

    // On click of search button in front end control comes here.
    public void search_api(View view) {
        View ab = this.getCurrentFocus();
        if (ab != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        /*if((email.getText().toString().equals("janani@plc.com") )&& (password.getText().toString().equals("Maziya"))){*/
        bus = "";
        details.setText("");
        origin = from.getText().toString().trim();
        destination = to.getText().toString().trim();
        String a = "Purasaiwalkam", b = "Besant Nagar";
        if (origin.isEmpty()) {
            Toast.makeText(this, "Please enter origin address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(this, "Please enter destination address!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Calling Google Transit API.
        try {
            new LocationFinder(this, origin, destination).execute(); // new object is created for the class Location finder. and the execute function of that class is called.
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onDirectionFinderStart() {

    }

    // When API call is sucess and response comes control comes here.
    @Override
    public void onDirectionFinderSuccess(Route route) {
        bus = details.getText().toString();
        if (j == 0) {
            dep = route.dep_name;
            dep_time = route.departure_time;
            bus_no = route.short_name;
            j++;
        }
        // Displaying result in textView.
        details.setText(bus + route.html_instructions + "\n Bus No :" + route.short_name + "\n Depature Point: " + route.dep_name + "\nTime: " + route.departure_time + "\nArrival Point: " + route.arr_name + "\n\n");
    }


    //Signout
    public void signout(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    //Taking to the Maps Activity on clicking the result

    public void track1(View view) {
        Intent i = new Intent(this, TrackingActivity.class);
        Bundle extras = new Bundle();
        extras.putString("dep_name", dep);
        extras.putString("bus_no", bus_no);
        extras.putString("dep_time", dep_time);
        j = 0;
        i.putExtras(extras);
        startActivity(i);
    }
}
