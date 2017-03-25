// This is the Maps Activity.. where the bus is shown

package com.vuedata.bustrackingapp;

import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TrackingActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = TrackingActivity.class.getName();
    TextView rem_minutes;
    double lat, longi;
    private GoogleMap mMap;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        Bundle extras = getIntent().getExtras();
        String dep_name = extras.getString("dep_name");
        String dep_time = extras.getString("dep_time");
        String bus_no = extras.getString("bus_no");
        String time_now = new SimpleDateFormat("h:mm a", java.util.Locale.getDefault()).format(new Date());
        Date Date2 = null, Date1 = null;
        try {
            SimpleDateFormat format = new SimpleDateFormat("h:mm a");
            Date2 = format.parse(dep_time);
            Date1 = format.parse(time_now);
            long difference = Date2.getTime() - Date1.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        /*long mills = Date1.getTime() - Date2.getTime();
        int Hours =(int) mills/(1000 * 60 * 60);
        int Mins = (int) mills % (1000*60*60);
        String diff = Hours + ":" + Mins;*/
        /*SimpleDateFormat format = new SimpleDateFormat("HH:MM");
        Date date1 = null, date2 = null;
        try {
            date2 = format.parse(dep_time);
            date1 = format.parse(time_now);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        long difference = date2.getTime() - date1.getTime();*/
        rem_minutes = (TextView) findViewById(R.id.rem_minutes);
        rem_minutes.setText(bus_no + " will Reach " + dep_name + " at " + dep_time);
    }

    @Override
    public void onBackPressed() {
        rem_minutes = (TextView) findViewById(R.id.rem_minutes);
        rem_minutes.setText("");
        finish();

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("location");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                //Getting Values from Firebase ..
                lat = dataSnapshot.child("lat").getValue(Double.class);
                longi = dataSnapshot.child("long").getValue(Double.class);
                Log.d(TAG, "lat is: " + lat);
                Log.d(TAG, "long is: " + longi);
                LatLng sydney = new LatLng(lat, longi);
                mMap.addMarker(new MarkerOptions().position(sydney).title("MTC M70- CMBT To THIRUVANMIYUR ")).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.modern_bus1));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, longi), 15));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(13.0823, 80.2754);

        // Default Bus
        mMap.addMarker(new MarkerOptions().position(sydney).title("MTC 17D- Broadway To K.K Nagar ")).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.modern_bus1));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(13.0823, 80.2754), 10));
        if (checkPermission())
            mMap.setMyLocationEnabled(true);
        /*else askPermission();*/

    }

    // Check for permission to access Location
    private boolean checkPermission() {
        Log.d(TAG, "checkPermission()");
        // Ask for permission if it wasn't granted yet
        return (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }
    // Asks for permission
    /*private void askPermission() {
        Log.d(TAG, "askPermission()");
        ActivityCompat.requestPermissions(
                this,
                new String[] { android.Manifest.permission.ACCESS_FINE_LOCATION }*//* REQ_PERMISSION*//*);
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult()");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        /*switch ( requestCode ) {
            case //REQ_PERMISSION: {
                if ( grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                    // Permission granted
                    if(checkPermission())
                        mMap.setMyLocationEnabled(true);

                } else {
                    // Permission denied

                }
                break;
            }*/
        //}
    }

}
