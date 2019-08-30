package com.amaya.speedvolume;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    // Global properties
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final String TAG = "MainActivity";

    LocationListener locationListener;
    LocationManager locationManager;
    AudioManager audioManager;

    DecimalFormat df;
    DecimalFormat dfAccuracy;

    Integer speed_level_1 = 0;
    Integer speed_level_2 = 0;
    Integer speed_level_3 = 0;
    Integer speed_level_4 = 0;
    Integer speed_level_5 = 0;

    Double volume_level_1 = 0.2;
    Double volume_level_2 = 0.3;
    Double volume_level_3 = 0.5;
    Double volume_level_4 = 0.7;
    Double volume_level_5 = 0.85;
    // ---

    // UI
    private Switch switch_activate;
    private EditText et_30;
    private EditText et_50;
    private EditText et_70;
    private EditText et_85;
    private EditText et_100;
    private TextView tv_speed;
    private TextView tv_accuracy_speed;
    // ---

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        setUI();
        setListeners();
        initializeActivity();

    }

    // Functions
    private void setUI() {
        switch_activate = (Switch) this.findViewById(R.id.switch_activate);
        et_30 = (EditText) this.findViewById(R.id.et_30);
        et_50 = (EditText) this.findViewById(R.id.et_50);
        et_70 = (EditText) this.findViewById(R.id.et_70);
        et_85 = (EditText) this.findViewById(R.id.et_85);
        et_100 = (EditText) this.findViewById(R.id.et_100);
        tv_speed = (TextView) this.findViewById(R.id.tv_speed);
        tv_accuracy_speed = (TextView) this.findViewById(R.id.tv_accuracy_speed);

        switch_activate.setChecked(false);
    }

    private void setListeners() {
        switch_activate.setOnCheckedChangeListener(new OnCheckedChangeListener());
    }

    private void initializeActivity() {

            // Location manager
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            // Location listener
            locationListener = new LocationListener();

            audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
            // Utils
            df = new DecimalFormat("#");
            dfAccuracy = new DecimalFormat("#.##");
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


    private void setSpeedText(Location location) {
        if (location != null) {
            String speed = df.format(location.getSpeed()/3.6);
            String accuracySpeed = dfAccuracy.format(location.getSpeedAccuracyMetersPerSecond()/3.6);
            tv_speed.setText(speed);
            tv_accuracy_speed.setText(accuracySpeed);
        } else {
            tv_speed.setText("0");
            tv_accuracy_speed.setText("0.00");
        }
    }


    private void setVolumeLevel(Location location) {
        Log.i(TAG, "setVolumeLevel: hola");

        int speed = Math.round(location.getSpeed());
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int newVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        if (speed <= speed_level_1) {
            newVolume = (int) (maxVolume * volume_level_1);
        } else if (speed > speed_level_1 && speed <= speed_level_2) {
            newVolume = (int) (maxVolume * volume_level_2);
        } else if (speed > speed_level_2 && speed <= speed_level_3) {
            newVolume = (int) (maxVolume * volume_level_3);
        } else if (speed > speed_level_3 && speed <= speed_level_4) {
            newVolume = (int) (maxVolume * volume_level_4);
        } else if (speed > speed_level_4 && speed <= speed_level_5) {
            newVolume = (int) (maxVolume * volume_level_5);
        } else if (speed > speed_level_5) {
            newVolume = maxVolume;
        }
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0);
    }

    private void setSpeedLevels() {
        // que pasa si getText está sin rellenar
        speed_level_1 = Integer.valueOf(et_30.getText().toString());
        speed_level_2 = Integer.valueOf(et_50.getText().toString());
        speed_level_3 = Integer.valueOf(et_70.getText().toString());
        speed_level_4 = Integer.valueOf(et_85.getText().toString());
        speed_level_5 = Integer.valueOf(et_100.getText().toString());
    }


    private void showDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Los campos están vacíos");
        alertDialog.setMessage("Debe rellenar los niveles de velocidad");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
    // ---

    // Listeners
    private class OnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            if (et_30.getText().length() > 0
                    && et_50.getText().length() > 0
                    && et_70.getText().length() > 0
                    && et_85.getText().length() > 0
                    && et_100.getText().length() > 0) {
                if (isChecked) {
                    // Activamos el servicio
                    startLocationRequestUpdates();
                    setSpeedLevels();
                } else {
                    // Desactivamos el servicio
                    stopLocationRequestUpdates();
                    setSpeedText(null);
                }
            } else {
                showDialog();
                switch_activate.setChecked(false);
            }

        }
    }


    private class LocationListener implements android.location.LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            setVolumeLevel(location);
            setSpeedText(location);
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
                    startLocationRequestUpdates();
                } else {
                    Toast.makeText(this, "Es necesario aceptar los permisos", Toast.LENGTH_LONG).show();
                }
                return;
            }

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
    }

    // ---
}
