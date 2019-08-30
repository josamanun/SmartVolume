package com.amaya.speedvolume;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // Global properties
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final String TAG = "MainActivity";

    private Activity globalActivity;
    private Context globalContext;

    private boolean isActivated;
    LocationListener locationListener;
    LocationManager locationManager;
    // ---

    // UI
    private Switch switch_activate;
    private EditText et_30;
    private EditText et_50;
    private EditText et_70;
    private EditText et_85;
    private EditText et_100;
    private TextView tv_speed;
    // ---

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        setUI();
        setListeners();
        initializeActivity();

    }

    private void setUI() {
        switch_activate = (Switch) this.findViewById(R.id.switch_activate);
        et_30 = (EditText) this.findViewById(R.id.et_30);
        et_50 = (EditText) this.findViewById(R.id.et_50);
        et_70 = (EditText) this.findViewById(R.id.et_70);
        et_85 = (EditText) this.findViewById(R.id.et_85);
        et_100 = (EditText) this.findViewById(R.id.et_100);
        tv_speed = (TextView) this.findViewById(R.id.tv_speed);

        switch_activate.setChecked(false);
    }

    private void setListeners() {
        switch_activate.setOnCheckedChangeListener(new OnCheckedChangeListener());
    }

    private void initializeActivity() {


            // Acquire a reference to the system Location Manager
            locationManager = (LocationManager) this
                    .getSystemService(Context.LOCATION_SERVICE);

            locationListener = new LocationListener();

    }

    private void startLocationRequestUpdates() {
        // Check for location permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkLocationPermission();
        } else {
            //Location Permission already granted
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1500,
                    0, locationListener);
        }
    }

    private void stopLocationRequestUpdates() {
        locationManager.removeUpdates(locationListener);
    }

    private void checkLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_LOCATION);
    }

    // Listeners
    private class OnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            isActivated = isChecked;

            if (isChecked) {
                // Activamos el servicio
                startLocationRequestUpdates();
            } else {
                // Desactivamos el servicio
                stopLocationRequestUpdates();
            }
        }
    }

    private class LocationListener implements android.location.LocationListener {

        @Override
        public void onLocationChanged(Location location) {

            String speed =  String.format("%.02f", location.getSpeedAccuracyMetersPerSecond()/3.6);
            tv_speed.setText(speed + " km/h");

            Log.i(TAG, "onLocationChanged: speed: " + location.getSpeed());
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
    // ---

    // Override

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initializeActivity();
                } else {
                    Toast.makeText(this, "Es necesario aceptar los permisos", Toast.LENGTH_LONG).show();
                }
                return;
            }

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    // ---
}
