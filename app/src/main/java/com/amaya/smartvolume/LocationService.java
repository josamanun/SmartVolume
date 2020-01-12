package com.amaya.smartvolume;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import static com.amaya.smartvolume.MainActivity.BROADCAST_ACTION;
import static com.amaya.smartvolume.MainActivity.LOCATION_ACCURATE_EXTRA;
import static com.amaya.smartvolume.MainActivity.LOCATION_EXTRA;
import static com.amaya.smartvolume.MainActivity.speed_level_1_text;
import static com.amaya.smartvolume.MainActivity.speed_level_2_text;
import static com.amaya.smartvolume.MainActivity.speed_level_3_text;
import static com.amaya.smartvolume.MainActivity.speed_level_4_text;
import static com.amaya.smartvolume.MainActivity.speed_level_5_text;

public class LocationService extends Service {

    private static String TAG = "LocationService";
    private static Integer REFRESH_FREQUENCY = 500;

    public LocationManager locationManager;
    public LocationListener locationListener;

    Intent intent;

    Notification notification;

    static Integer speed_level_1;
    static Integer speed_level_2;
    static Integer speed_level_3;
    static Integer speed_level_4;
    static Integer speed_level_5;

    @Override
    public void onCreate() {
        // Location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Location listener
        locationListener = new LocationListener();

        intent = new Intent(BROADCAST_ACTION);

        initializeForegroundNotification();
        startForeground(1, notification);

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Check for location permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            MainActivity.checkLocationPermission();
        } else {
            setSpeedLevels(intent);
            //Location Permission already granted
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, REFRESH_FREQUENCY,
                    0, locationListener);
        }
        startForeground(1, notification);
        return START_STICKY;
    }

    private void initializeForegroundNotification() {
        String CHANNEL_ID = "my_channel_01";
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT);

        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("")
                .setContentText("").build();
    }

    private void setSpeedLevels(Intent intent) {
        speed_level_1 = intent.getIntExtra(speed_level_1_text, 0);
        speed_level_2 = intent.getIntExtra(speed_level_2_text, 0);
        speed_level_3 = intent.getIntExtra(speed_level_3_text, 0);
        speed_level_4 = intent.getIntExtra(speed_level_4_text, 0);
        speed_level_5 = intent.getIntExtra(speed_level_5_text, 0);
    }


    private class LocationListener implements android.location.LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            intent.putExtra(LOCATION_EXTRA, location.getSpeed());
            intent.putExtra(LOCATION_ACCURATE_EXTRA, location.getSpeedAccuracyMetersPerSecond());
            sendBroadcast(intent);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
}
