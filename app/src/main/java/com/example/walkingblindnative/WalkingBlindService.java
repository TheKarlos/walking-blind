package com.example.walkingblindnative;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class WalkingBlindService extends Service implements LocationListener{
    public int counter=0;

    public boolean isGPSEnabled;
    public boolean isNetworkEnabled;
    public boolean locationServiceAvailable;

    //The minimum distance to change updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10 meters

    //The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 0;//1000 * 60 * 1; // 1 minute

    private final static boolean forceNetwork = false;

    private static WalkingBlindService instance = null;

    private LocationManager locationManager;
    public Location location;
    public double longitude;
    public double latitude;
    public static TTSManager ttsManager = null;

    /**
     * Singleton implementation
     * @return
     */
    public static WalkingBlindService getLocationManager(Context context)     {
        if (instance == null) {
            instance = new WalkingBlindService();
        }
        return instance;
    }



    /**
     * Local constructor
     */
    public WalkingBlindService()     {
       // instance = this;
        initLocationService(MainActivity.getContext());
        this.ttsManager = MainActivity.ttsManager;
    }



    /**
     * Sets up location service after permissions is granted
     */
    @TargetApi(23)
    private void initLocationService(Context context) {
        Log.i("Count", context.toString());

        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        try {
            this.longitude = 0.0;
            this.latitude = 0.0;
            this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            Log.i("count", "Location Manager made");

            // Get GPS and network status
            this.isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            this.isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (forceNetwork) isGPSEnabled = false;

            if (!isNetworkEnabled && !isGPSEnabled) {
                // cannot get location
                this.locationServiceAvailable = false;
            }
            //else
            {
               this.locationServiceAvailable = true;

                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                           MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }//end if

                if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }
                }

                Log.i("count", "GPS STATIUS: " + isGPSEnabled);
           }
        } catch (Exception ex) {
            Log.i("count", "Error creating location service: " + ex.getMessage());

        }
    }


    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onLocationChanged(Location location)     {
        Log.i("Count", "Just recieved update: "  + location.toString());

        JSONArray obj = null;

        try{
            obj = getHTTP(location.getLatitude(), location.getLongitude());
        }catch (Exception e) {

        }
        if(obj != null){
            Log.i("CountAAAA", obj.toString());

        }else{
            Log.i("Count", "Null response from server");
        }

    }

    public JSONArray getHTTP(double lat, double lon) throws MalformedURLException, IOException {
        Log.i("count", "A-1");
        Log.i("count", "10.41.40.205:8080/app/getLocationPOIs?lat=" + lat + "&lon=" + lon);

        Log.i("count", "A0");



        JSONArray json = null;
        try {
            Log.i("count", "A1");
            Log.i("count", "A2");
            RequestQueue queue = Volley.newRequestQueue(this);
            String url ="http://10.41.40.205:8080/app/getLocationPOIs?lat=" + lat + "&lon=" + lon;

            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("TAG", response);
                            Log.i("count", "A3");
                            try {
                                JSONArray json  = new JSONArray(response);
                                ttsManager.initQueue(TextGenerator.getMessage(latitude, longitude, json));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("TAG", "CRYPT not work" + error.getMessage());
                }
            });

// Add the request to the RequestQueue.
            queue.add(stringRequest);


        }catch(Exception e){

        }

        return json;
    }

    private JSONObject getJsonObject(InputStream inputStream){
        try {
            Log.i("count", "getOBJs");
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;

            while ((inputStr = streamReader.readLine()) != null) {
                responseStrBuilder.append(inputStr);
                Log.i("count", inputStr);
            }


            Log.i("count", responseStrBuilder.toString());
            JSONObject jsonObject = new JSONObject(responseStrBuilder.toString());

            //returns the json object
            return jsonObject;

        } catch (IOException e) {
            Log.i("count", "IOEXception");
            e.printStackTrace();
        } catch (JSONException e) {
            Log.i("count", "JSON Exceptions");
            e.printStackTrace();
        }

        //if something went wrong, return null
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground()
    {
        String NOTIFICATION_CHANNEL_ID = "example.permanence";
        String channelName = "Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startTimer();
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stoptimertask();
    }


    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        //TODO
    }

    private Timer timer;
    private TimerTask timerTask;
    public void startTimer() {

        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {
                Log.i("Count", "=========  "+ (counter++));

               // if(isGPSEnabled)
                //{
                //    Log.i("Count", "" + locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude());
                //}

            }
        };
        timer.schedule(timerTask, 100, 1000); //
    }

    public void stoptimertask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}