package jugendhacktsued.com.dreckweggapp;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MapView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);
        Intent intent = getIntent();
        int lat = intent.getIntExtra("lat", 0);
        int lon = intent.getIntExtra("lon", 0);

        WebView webview = new WebView(this);
        setContentView(webview);
        webview.setWebViewClient(new WebViewClient());
        //No need to worry about security
        webview.getSettings().setJavaScriptEnabled(true);

        //webview.loadUrl("http://www.opentouchmap.org/?lat=" + lat + "&lon=" + lon + "&zoom=15");
        webview.loadUrl("http://10.0.15.91/dreckwegg/map");


        //TODO: what to do here??
        if(android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            webview.evaluateJavascript("", null);
        }

    }
}
