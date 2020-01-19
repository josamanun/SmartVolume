package com.amaya.smartvolume.services;

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

import com.amaya.smartvolume.activities.MainActivity;
import com.amaya.smartvolume.utils.Logger;

import static com.amaya.smartvolume.activities.MainActivity.BROADCAST_ACTION;
import static com.amaya.smartvolume.activities.MainActivity.LOCATION_EXTRA;

public class LocationService extends Service {

    private static String TAG = "LocationService";
    private static Integer REFRESH_FREQUENCY = 500;

    public LocationManager locationManager;
    public LocationListener locationListener;

    Intent intent;

    Notification notification;

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

    private class LocationListener implements android.location.LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            Logger.logOnNote("location.getSpeed(): " + location.getSpeed());
            intent.putExtra(LOCATION_EXTRA, location.getSpeed());
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
