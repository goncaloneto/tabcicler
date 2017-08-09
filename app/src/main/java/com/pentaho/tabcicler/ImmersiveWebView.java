package com.pentaho.tabcicler;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ImmersiveWebView extends AppCompatActivity {

    private WebView mWebView;
    private int i = 0;
    private List<String> urls;
    private List<String> durations;
    private final int DEFAULT_DURATION = 10;

    public void nextUrl(){

        if( i < urls.size() -1 ){
            i = i + 1;
        } else {
            i = 0;
        }

        mWebView.loadUrl(urls.get(i));
    }

    public int getDelay(){
        int delay;

        try{
            delay = Integer.parseInt( durations.get(i) );
        } catch( NumberFormatException e ) {
            Log.e( this.toString(), "Invalid Duration. " + e.getMessage() );
            return DEFAULT_DURATION * 1000;
        }
        return delay * 1000;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        urls = (ArrayList<String>) intent.getStringArrayListExtra( "list" );
        durations = (ArrayList<String>) intent.getStringArrayListExtra( "durationList" );

        mWebView = new WebView( this );

        mWebView.setSystemUiVisibility(
                WebView.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | WebView.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | WebView.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | WebView.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | WebView.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | WebView.SYSTEM_UI_FLAG_IMMERSIVE);

        mWebView.getSettings().setJavaScriptEnabled(true); // enable javascript

        if (Build.VERSION.SDK_INT >= 19) {
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        else {
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        final Activity activity = this;

        mWebView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, description, Toast.LENGTH_SHORT).show();
            }
        });

        //Load the first web page
        if( urls.size() > 0 ){
            mWebView.loadUrl( urls.get(0) );
        }

        final Handler handler = new Handler();

        handler.postDelayed(new Runnable(){
            public void run(){
                if( urls.size() > 1 )
                    nextUrl();
                //if this is removed, when the user taps the screen it will exit immersive mode
                mWebView.setSystemUiVisibility(
                        WebView.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | WebView.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | WebView.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | WebView.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | WebView.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | WebView.SYSTEM_UI_FLAG_IMMERSIVE);
                handler.postDelayed( this, getDelay() );
            }
        }, getDelay() );


        setContentView( mWebView );

    }

}
