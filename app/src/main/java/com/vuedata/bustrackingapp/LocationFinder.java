// Calling Google Transit API

package com.vuedata.bustrackingapp;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maz Vuedata on 14-02-2017.
 */

public class LocationFinder {

    private static final String DIRECTION_URL_API = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String GOOGLE_API_KEY = "AIzaSyC5Y3xe5iCnTeCxyPF-nzQmPj2j-KrZICU";
    private static final String TAG = SearchGoogleApiActivity.class.getName();
    private LocationFinderListener listener;
    private String origin;
    private String destination;

    public LocationFinder(LocationFinderListener listener, String origin, String destination) {
        this.listener = listener;
        this.origin = origin;
        this.destination = destination;
    }

    // Control comes here from SearchGoogle API activity
    public void execute() throws UnsupportedEncodingException {
        listener.onDirectionFinderStart();// If we want to do anything to notify the api call is started we can do it here.
        new DownloadRawData().execute(createUrl()); // API calling Begins here... Before that REST API URL is generated in the Create URL function
    }

    //This Fucntion creates the google Transit URL.  It is a GET api. so we will send the origin and desitination from the front end and send it with url
    //also we have modified the url such that it returns transit route and only bus routes. ( trains and others are omiited)
    private String createUrl() throws UnsupportedEncodingException {
        String urlOrigin = URLEncoder.encode(origin, "utf-8");
        String urlDestination = URLEncoder.encode(destination, "utf-8");

        String a = DIRECTION_URL_API + "origin=" + urlOrigin + "&destination=" + urlDestination + "&mode=transit&transit_mode=bus&key=" + GOOGLE_API_KEY;
        return a;
    }

    // This class calls the Google API and downloads the response..

    private void parseJSon(String data) throws JSONException {
        if (data == null)
            return;

       /* List<Route> routes = new ArrayList<Route>();*/
        JSONObject jsonData = new JSONObject(data);
        JSONArray jsonRoutes = jsonData.getJSONArray("routes");
        for (int i = 0; i < jsonRoutes.length(); i++) {
            JSONObject jsonRoute = jsonRoutes.getJSONObject(i);
            Route route = new Route();

            JSONObject overview_polylineJson = jsonRoute.getJSONObject("overview_polyline");
            JSONArray jsonLegs = jsonRoute.getJSONArray("legs");
            JSONObject jsonLeg = jsonLegs.getJSONObject(0);
            JSONObject jsonDistance = jsonLeg.getJSONObject("distance");
            JSONObject jsonDuration = jsonLeg.getJSONObject("duration");
            JSONObject jsonEndLocation = jsonLeg.getJSONObject("end_location");
            JSONObject jsonStartLocation = jsonLeg.getJSONObject("start_location");

            JSONArray jsonSteps = jsonLeg.getJSONArray("steps");
            for (int j = 0; j < jsonSteps.length(); j++) {
                JSONObject jsonstep = jsonSteps.getJSONObject(j);
                String jsonTravelMode = jsonstep.getString("travel_mode");

                // Getting all the details of the bus and storing it in a class.

                if (jsonTravelMode.equals("TRANSIT")) {
                    JSONObject jsonTransitDetails = jsonstep.getJSONObject("transit_details");
                    route.html_instructions = jsonstep.getString("html_instructions");
                    JSONObject departure = jsonTransitDetails.getJSONObject("departure_time");
                    route.departure_time = departure.getString("text");
                    JSONObject dep_point = jsonTransitDetails.getJSONObject("departure_stop");
                    route.dep_name = dep_point.getString("name");
                    JSONObject arr_point = jsonTransitDetails.getJSONObject("arrival_stop");
                    route.arr_name = arr_point.getString("name");
                    JSONObject jsonLine = jsonTransitDetails.getJSONObject("line");
                    route.short_name = jsonLine.getString("short_name");
                    listener.onDirectionFinderSuccess(route); //When everthing is done, On directionFinder Sucess function is called.
                }
            }
            /*route.distance = new Distance(jsonDistance.getString("text"), jsonDistance.getInt("value"));
            route.duration = new Duration(jsonDuration.getString("text"), jsonDuration.getInt("value"));
            route.endAddress = jsonLeg.getString("end_address");
            route.startAddress = jsonLeg.getString("start_address");
            route.startLocation = new LatLng(jsonStartLocation.getDouble("lat"), jsonStartLocation.getDouble("lng"));
            route.endLocation = new LatLng(jsonEndLocation.getDouble("lat"), jsonEndLocation.getDouble("lng"));
            route.points = decodePolyLine(overview_polylineJson.getString("points"));

            routes.add(route); */
        }

        /*listener.onDirectionFinderSuccess(routes);*/
    }

    // Its an async task and the process goes asynchorously in background.
    private class DownloadRawData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String link = params[0];
            try {
                URL url = new URL(link);
                InputStream is = url.openConnection().getInputStream();
                StringBuffer buffer = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        // When API call is done here comes the control with The responsse.
        @Override
        protected void onPostExecute(String res) {
            Log.d(TAG, "Bus is: " + res);
            try {
                parseJSon(res); // We are calling parsing the JSON file received so that we can use it.
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
