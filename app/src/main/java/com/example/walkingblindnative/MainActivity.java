package com.example.walkingblindnative;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private boolean isStarted = false;

    Intent mServiceIntent;
    private WalkingBlindService mYourService;

    private static Context mContext;

    public static TTSManager ttsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getApplicationContext();
        setContentView(R.layout.activity_main);



        Log.d("applog", "App crypt running");

        final Button button = (Button) findViewById(R.id.button);
        button.setText(getResources().getString(R.string.start_button));//set the text on button
        button.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(isStarted){
                    button.setText(getResources().getString(R.string.start_button));//set the text on button
                    button.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                    stopDesribeEngine();
                }else{
                    button.setText(getResources().getString(R.string.stop_button));//set the text on button
                    button.setBackgroundColor(getResources().getColor(R.color.colorRed));

                    startDesribeEngine();
                }isStarted = !isStarted;
            }
        });

        ttsManager = new TTSManager();
        ttsManager.init(mContext);

        //ttsManager.initQueue("Welcome to Blind Walk. Tap the centre of the screen to start.");
        //ttsManager.addQueue("Welcome to Blind Walk. Tap the centre of the screen to start.");
    }

    public static Context getContext() {
        //  return instance.getApplicationContext();
        return mContext;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("Service status", "Running");
                return true;
            }
        }
        Log.i ("Service status", "Not running");
        return false;
    }


    @Override
    protected void onDestroy() {
        stopService(mServiceIntent);
        super.onDestroy();
    }

    private void startDesribeEngine(){
        ttsManager.initQueue("App Started. Start Walking to hear Audio Description of your surroundings");
        //ttsManager.addQueue("Welcome to Blind Walk. Tap the centre of the screen to start.");

        mYourService = new WalkingBlindService();
        mServiceIntent = new Intent(this, mYourService.getClass());
        if (!isMyServiceRunning(mYourService.getClass())) {
            startService(mServiceIntent);
        }

    }

    private void stopDesribeEngine(){
        stopService(mServiceIntent);
        ttsManager.initQueue("App Stopped");
    }
}
