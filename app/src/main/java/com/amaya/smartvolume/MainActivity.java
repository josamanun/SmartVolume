package com.amaya.smartvolume;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    public static final String BROADCAST_ACTION = "JOSELITO";
    public static final String LOCATION_EXTRA = "LOCATION_EXTRA";
    public static final String LOCATION_ACCURATE_EXTRA = "LOCATION_ACCURATE_EXTRA";

    static Activity globalActivity;

    static Intent locationIntent;
    BroadcastReceiver locationRefreshReceiver;
    static AudioManager audioManager;

    static DecimalFormat df;
    static DecimalFormat dfAccuracy;

    static String speed_level_1_text = "speed_level_1";
    static String speed_level_2_text= "speed_level_2";
    static String speed_level_3_text = "speed_level_3";
    static String speed_level_4_text = "speed_level_4";
    static String speed_level_5_text = "speed_level_5";

    static Integer speed_level_1 = 0;
    static Integer speed_level_2 = 0;
    static Integer speed_level_3 = 0;
    static Integer speed_level_4 = 0;
    static Integer speed_level_5 = 0;

    static Double volume_level_1 = 0.2;
    static Double volume_level_2 = 0.3;
    static Double volume_level_3 = 0.5;
    static Double volume_level_4 = 0.7;
    static Double volume_level_5 = 0.85;
    // ---

    // UI
    private Switch switch_activate;
    private Switch switch_speed_accurate;
    private EditText et_30;
    private EditText et_50;
    private EditText et_70;
    private EditText et_85;
    private EditText et_100;
    static TextView tv_speed;
    static TextView tv_accuracy_speed;
    // ---

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        globalActivity = this;

        setUI();
        setListeners();
        initializeActivity();

    }

    // Functions
    private void setUI() {
        switch_activate = (Switch) this.findViewById(R.id.switch_activate);
        switch_speed_accurate = (Switch) this.findViewById(R.id.switch_speed_accurate);
        et_30 = (EditText) this.findViewById(R.id.et_30);
        et_50 = (EditText) this.findViewById(R.id.et_50);
        et_70 = (EditText) this.findViewById(R.id.et_70);
        et_85 = (EditText) this.findViewById(R.id.et_85);
        et_100 = (EditText) this.findViewById(R.id.et_100);
        tv_speed = (TextView) this.findViewById(R.id.tv_speed);
        tv_accuracy_speed = (TextView) this.findViewById(R.id.tv_accuracy_speed);

        switch_activate.setChecked(false);
        switch_speed_accurate.setChecked(false);
    }

    private void setListeners() {
        switch_activate.setOnCheckedChangeListener(new OnActivateCheckedChangeListener());
    }

    private void initializeActivity() {

            locationIntent = new Intent(globalActivity, LocationService.class);
            locationRefreshReceiver = new LocationRefreshReceiver();
            globalActivity.registerReceiver(locationRefreshReceiver, new IntentFilter(BROADCAST_ACTION));

            audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
            // Utils
            df = new DecimalFormat("#");
            dfAccuracy = new DecimalFormat("#.##");
    }

    private void startLocationRequestUpdates() {
        locationIntent.putExtra(speed_level_1_text, speed_level_1);
        locationIntent.putExtra(speed_level_2_text, speed_level_2);
        locationIntent.putExtra(speed_level_3_text, speed_level_3);
        locationIntent.putExtra(speed_level_4_text, speed_level_4);
        locationIntent.putExtra(speed_level_5_text, speed_level_5);

        // Start foreground location service
        ContextCompat.startForegroundService(globalActivity, locationIntent);
    }

    private void stopLocationRequestUpdates() {
        stopService(locationIntent);
    }

    public static void checkLocationPermission() {
        // First stop location service
        globalActivity.stopService(locationIntent);

        ActivityCompat.requestPermissions(globalActivity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_LOCATION);
    }


    public static void setSpeedText(float speedFloat, float speedAccurateFloat) {
        if (speedFloat != -1) {
            String speed = df.format(speedFloat/3.6);
            String accuracySpeed = dfAccuracy.format(speedAccurateFloat/3.6);
            tv_speed.setText(speed);
            tv_accuracy_speed.setText(accuracySpeed);
        } else {
            tv_speed.setText("0");
            tv_accuracy_speed.setText("0.00");
        }
    }

    public static void setVolumeLevel(float speedFloat) {

        int speed = Math.round(speedFloat);
        int maxVolume;
        int newVolume;
        if (audioManager.getMode() == AudioManager.MODE_IN_CALL) {
            maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
            newVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
        } else {
            maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            newVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        }

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
        if (audioManager.getMode() == AudioManager.MODE_IN_CALL) {
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, newVolume, 0);
        } else {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0);
        }
    }

    private void setSpeedLevels() {
        speed_level_1 = Integer.valueOf(et_30.getText().toString());
        speed_level_2 = Integer.valueOf(et_50.getText().toString());
        speed_level_3 = Integer.valueOf(et_70.getText().toString());
        speed_level_4 = Integer.valueOf(et_85.getText().toString());
        speed_level_5 = Integer.valueOf(et_100.getText().toString());
    }

    private void showDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Los campos están vacíos");
        alertDialog.setMessage("Debe introducir los niveles de velocidad");
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
    private class OnActivateCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {

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
                    setSpeedText(-1, -1);
                }
            } else {
                showDialog();
                switch_activate.setChecked(false);
            }

        }
    }
    // ---

    // Override
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationRequestUpdates();
                } else {
                    Toast.makeText(this, "Es necesario aceptar los permisos", Toast.LENGTH_LONG).show();
                    switch_activate.setChecked(false);
                }
                return;
            }

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onDestroy() {
        stopLocationRequestUpdates();
        super.onDestroy();
    }
    // ---

    // Location receiver
    private class LocationRefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            float speed = intent.getFloatExtra(LOCATION_EXTRA, 0);
            float speedAccurate = intent.getFloatExtra(LOCATION_ACCURATE_EXTRA, 0);
            // Check if accurate speed switch is checked to use speedAccurate
            if (switch_speed_accurate.isChecked()) {
                setVolumeLevel(speedAccurate);
            } else {
                setVolumeLevel(speed);
            }
            setSpeedText(speed, speedAccurate);
        }
    }
    // ---
}
