package jugendhacktsued.com.dreckweggapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import org.apache.http.util.EntityUtils;
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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Provider;

public class MainActivity extends AppCompatActivity {


    final static String serviceCode = "1234";
    final static String URLofServer = "http://10.0.15.91:80/dreckwegg/georeport/v2/requests.json";
    double lat = 1;
    double lon = 1;
    TextView debugText;
    ImageButton dreckButton;
    String provider; //provider for Location Data

    LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dreckButton = (ImageButton)findViewById(R.id.dreck_button);

        //get location manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        //get the best provider
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, true);

        Location loc;

        //get coordinates for the first time
        try {
            loc = this.locationManager.getLastKnownLocation(provider);
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

    }

    //opens the map when map is clicked
    public void onMap(View view){
        Intent intent = new Intent(this, MapView.class);
        intent.putExtra("lat", lat);
        intent.putExtra("lon", lon);
        startActivity(intent);
    }

    public void dreckButton(View view) {

        //changes the button to button mit leiste and provides feedback
        dreckButton.setImageResource(R.drawable.dreck_button_mit_leiste);
        //changes the color back
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dreckButton.setImageResource(R.drawable.dreck_button);
            }
        }, 1000);

        //get location and set lat and lon
        //get coordinates
        try {
            locationManager.requestSingleUpdate(provider, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    lat = location.getLatitude();
                    lon = location.getLongitude();
                }

                @Override
                public void onProviderDisabled(String provider) {

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }
            }, null);
        }
        catch(SecurityException error){
            Log.d("Permissions", "most likely no permissions");
        }

        //this shows a toast with the lon at lat data
        Context context = getApplicationContext();
        CharSequence text = "Lat: " + lat + " Long: " + lon;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        //create and send json
        String urlEnc = createURLEnc(lat, lon, 1);
        new PostingJSON().execute(urlEnc);

    }


    //from this part on its about http and networks
    //creates a string that is correspondent to a json object
    private String createURLEnc(double lat, double lon, int dreckgrad){
        String urlEnc="";
        try {
            urlEnc += "service_code=" + serviceCode + "&lat=" + lat + "&long=" + lon + "&email=blubb@wuff.de";

            /*
            jsonObject.accumulate("service_code", serviceCode);
            jsonObject.accumulate("lat", lat);
            jsonObject.accumulate("long", lon);
            jsonObject.accumulate("email", "blubb@wuff.de");

            //jsonObject.accumulate("dreckgrad", dreckgrad);

            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();
            */
            Log.d("Json", urlEnc);
        }
        catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
        return urlEnc;
    }


    //this is the Async Task to do the posting of the json
    class PostingJSON extends AsyncTask<String, Void, Void> {

        private Exception exception;

        protected Void doInBackground(String... strings) {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(URLofServer);
            try {
                Log.d("Post-json", strings[0]);
                httppost.setEntity(new StringEntity(strings[0]));
                Log.d("Post-json", strings[0]);
                //httppost.setHeader("Accept", "application/json");
                httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");
                HttpResponse response = httpclient.execute(httppost);
                Log.d("Post", "Response: " + EntityUtils.toString(response.getEntity()));
            }
            catch(Exception error){
                Log.d("Post", "Error: " + error);
            }

            /*
            HttpURLConnection client = null;
            try {
                URL url = new URL(URLofServer); //need to insert ip address of server
                client = (HttpURLConnection) url.openConnection();
                client.setRequestMethod("POST");
                client.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
                client.setDoOutput(true);

                //writes json to the stream
                OutputStream outputPost = new BufferedOutputStream(client.getOutputStream());
                byte[] jsonBytes = strings[0].getBytes("UTF-8");
                outputPost.write(jsonBytes);
                //outputPost.flush(); //hopefully it will not stop before being finished
                //outputPost.close();


                Log.d("Post", "posted json");

            } catch (MalformedURLException error) {
                //Handles an incorrectly entered URL
                Log.d("Post", "incorrect URL" + error);
            } catch (SocketTimeoutException error) {
                //Handles URL access timeout.
                Log.d("Post", "access timeout" + error);
            } catch (IOException error) {
                //Handles input and output errors
                Log.d("Post", "ioerror" + error);
            } catch (Exception error) {
                Log.d("Post", "some weird error: " + error);
            }

            //close the connection again
            finally {
                if (client != null) // Make sure the connection is not null.
                    client.disconnect();
            }
            return null;
        }
        */
            return null;
        }

    }


}
