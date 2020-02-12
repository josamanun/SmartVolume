package com.amaya.smartvolume.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.amaya.smartvolume.utils.Calculator;
import com.amaya.smartvolume.utils.Logger;

import static com.amaya.smartvolume.data.DataGenerator.getFrequency;
import static com.amaya.smartvolume.fragments.HomeFragment.BROADCAST_ACTION;
import static com.amaya.smartvolume.fragments.HomeFragment.LOCATION_EXTRA;

public class LocationService extends Service {

    private static String TAG = "LocationService";

    public LocationManager locationManager;
    public LocationListener locationListener;

    Intent intent;

    Notification notification;

    Location mLastLocation;
    Long mLastLocationTime;

    @Override
    public void onCreate() {
        // Location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Location listener
        locationListener = new LocationListener();

        intent = new Intent(BROADCAST_ACTION);

        initializeForegroundNotification();

        super.onCreate();
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Check for location permission
            //Location Permission already granted
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                getRefreshFrequency(),
                0,
                locationListener);
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(1, notification);
        }
        return START_STICKY;
    }

    private long getRefreshFrequency() {
        return (long) (getFrequency()*1000);
    }

    private void initializeForegroundNotification() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();

            startForeground(1, notification);
        }
    }

    private class LocationListener implements android.location.LocationListener {

        @Override
        public void onLocationChanged(Location location) {

            float speed = -1;
            // Comprobar si es la primera localización
            if (mLastLocation != null && mLastLocationTime != null) {
                speed = calculateSpeed(location);
            }

            mLastLocation = location;
            mLastLocationTime = location.getTime();

            Log.i(TAG, "location.getSpeed(): " + location.getSpeed());
            Log.i(TAG, "calculateSpeed: " + speed);
            intent.putExtra(LOCATION_EXTRA, speed);
            sendBroadcast(intent);
        }

        private float calculateSpeed(Location newLocation) {
            try {
                Long m_distance = Calculator.calculateDistance(
                        mLastLocation.getLatitude(), mLastLocation.getLongitude(),
                        newLocation.getLatitude(), newLocation.getLongitude()
                );

                // Comprobamos que la distancia recorrida es suficiente como para calcular la velocidad
                if (m_distance < 4) {
                    return 0;
                }

                Long ms_time = newLocation.getTime() - mLastLocationTime;
                Long s_time = new Double(ms_time * 0.001).longValue();
                float speed_ms = m_distance / s_time;
                float speed_kmh = new Double(speed_ms * 3.6).floatValue();

                return speed_kmh;

            } catch (Exception e) {
                Log.e(TAG, "ERROR calculateSpeed: ", e);
                Logger.logOnNote("\nERROR in calculateSpeed: " + e.getMessage() + "\n");

                return -1;
            }
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
