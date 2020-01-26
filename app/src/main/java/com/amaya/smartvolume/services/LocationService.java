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
import android.util.Log;

import com.amaya.smartvolume.activities.MainActivity;
import com.amaya.smartvolume.data.SettingsData;
import com.amaya.smartvolume.utils.Calculator;
import com.amaya.smartvolume.utils.Logger;

import static com.amaya.smartvolume.activities.MainActivity.BROADCAST_ACTION;
import static com.amaya.smartvolume.activities.MainActivity.LOCATION_EXTRA;

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
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    getRefreshFrequency(),
                    0,
                    locationListener);
        }
        startForeground(1, notification);
        return START_STICKY;
    }

    private long getRefreshFrequency() {
        Integer refreshIndex = SharedPreferencesService.getIntegerItem(
                SettingsData.refresh_location_setting_id, -1);

        if(refreshIndex == -1) {
            return SettingsData.DEFAULT_REFRESH_FREQUENCY;
        }

        double refreshDoubleSeg = Double.parseDouble(
                SettingsData.refreshLocationSegOptions.get(refreshIndex)
        );
        return (long) (refreshDoubleSeg*1000);
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

            float speed = -1;
            // Comprobar si es la primera localización
            if (mLastLocation != null && mLastLocationTime != null) {
                speed = calculateSpeed(location);
            }

            Logger.logOnNote("location.getSpeed(): " + location.getSpeed());

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
                    Logger.logOnNote("Distance (m): " + m_distance +"\n");
                    return 0;
                }

                Long ms_time = newLocation.getTime() - mLastLocationTime;
                Long s_time = new Double(ms_time * 0.001).longValue();
                float speed_ms = m_distance / s_time;

//                Logger.logOnNote("Distance (m): " + m_distance);
//                Logger.logOnNote("Time (s): " + s_time);
                Logger.logOnNote("Speed (m/s): " + speed_ms+"\n");

                float speed_kmh = new Double(speed_ms * 3.6).floatValue();
//                Logger.logOnNote("Speed (km/h): " + speed_kmh+ "\n");

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
