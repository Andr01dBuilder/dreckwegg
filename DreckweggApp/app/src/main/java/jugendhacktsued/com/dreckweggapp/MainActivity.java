package jugendhacktsued.com.dreckweggapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    double lat = 1;
    double lon = 1;
    TextView debugText;
    Location locationWhole;  //location is valid in whole class
    LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        debugText = (TextView)findViewById(R.id.debug);


        //get location for the first time
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        Location loc;

        try {
            loc = this.locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Log.d("gps", "got locv" + loc);
            Log.i("Longitude", " " + loc.getLongitude());
        }
        catch(SecurityException error) {
            Log.d("gps", "problem with gps");
        }
        catch(IllegalArgumentException error){
            Log.d("gps", "illegal");
        }
        catch(Exception error){
            Log.d("gps", "what" + error);
        }

    }

    public void dreckButton(View view) {

        //get location and set lat and lon
        Location loc;
        try {
            loc = this.locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Log.d("gps", "got locv" + loc);
            Log.i("Longitude", " " + loc.getLongitude());
            lat = loc.getLatitude();
            lon = loc.getLongitude();
        }
        catch(SecurityException error) {
            Log.d("gps", "problem with gps");
        }
        catch(IllegalArgumentException error){
            Log.d("gps", "illegal");
        }
        catch(Exception error){
            Log.d("gps", "what" + error);
        }

        String json = createJson(lat, lon, 1);

        debugText.setText(json);

    }

    //for getting the location
    //from https://developer.android.com/guide/topics/location/strategies.html
    /* the following code drains the batteries
    public void getLocation() {
        //using https://developer.android.com/guide/topics/location/strategies.html
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                Log.i("coordinates", location.getLatitude() + " " + location.getLongitude());
                locationWhole = location;
                useCoordinates(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("location", "statuschanged");
            }

            public void onProviderEnabled(String provider) {
                Log.d("location", "providerenabled");
            }

            public void onProviderDisabled(String provider) {
                Log.d("location", "providerdisabled");
            }
        };

        // Register the listener with the Location Manager to receive location updates
        try
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }

    //uses location and puts coordinates
    private void useCoordinates(Location location){
        debugText.setText("Wuff1");
        lat = location.getLatitude();
        lon = location.getLongitude();

        debugText.setText(lat + " " + lon);

    }
    */

    //from this part on its about http and networks
    //creates a string that is correspondent to a json object
    private String createJson(double lat, double lon, int dreckgrad){
        String json="";
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("lat", lat);
            jsonObject.accumulate("lon", lon);
            jsonObject.accumulate("dreckgrad", dreckgrad);

            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();
        }
        catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
        return json;
    }

    private void postJSON(String json){
        HttpURLConnection client = null;
        try {
            URL url = new URL("http://10.0.15.162"); //need to insert ip address of server
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("POST");
            client.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
            client.setDoOutput(true);

            //writes json to the stream
            OutputStream outputPost = new BufferedOutputStream(client.getOutputStream());
            byte[] jsonBytes = json.getBytes("UTF-8");
            outputPost.write(jsonBytes);
            outputPost.flush(); //hopefully it will not stop before being finished
            outputPost.close();
        }

        catch(MalformedURLException error) {
            //Handles an incorrectly entered URL
        }
        catch(SocketTimeoutException error) {
            //Handles URL access timeout.
        }
        catch (IOException error) {
            //Handles input and output errors
        }

        //close the connection again
        finally {
            if(client != null) // Make sure the connection is not null.
                client.disconnect();
        }
    }


}
