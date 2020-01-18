package com.amaya.smartvolume.activities;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.amaya.smartvolume.R;
import com.amaya.smartvolume.services.LocationService;
import com.amaya.smartvolume.services.SharedPreferencesService;
import com.amaya.smartvolume.utils.Logger;

import java.text.DecimalFormat;
import java.util.HashMap;

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

    public static String speed_level_1_text = "speed_level_1";
    public static String speed_level_2_text= "speed_level_2";
    public static String speed_level_3_text = "speed_level_3";
    public static String speed_level_4_text = "speed_level_4";
    public static String speed_level_5_text = "speed_level_5";

    static Integer speed_level_1 = 0;
    static Integer speed_level_2 = 0;
    static Integer speed_level_3 = 0;
    static Integer speed_level_4 = 0;
    static Integer speed_level_5 = 0;

    static Double volume_level_max = 0.95;
    static Double volume_level_1 = 0.2;
    static Double volume_level_2 = 0.3;
    static Double volume_level_3 = 0.5;
    static Double volume_level_4 = 0.7;
    static Double volume_level_5 = 0.85;
    // ---

    // UI
    private Switch switch_activate;
    private Switch switch_speed_accurate;
    private EditText et_level_1;
    private EditText et_level_2;
    private EditText et_level_3;
    private EditText et_level_4;
    private EditText et_level_5;
    static TextView tv_speed;
    static TextView tv_accuracy_speed;
    private ImageView btn_save_values;
    private ImageView btn_delete_values;
    // ---

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        globalActivity = this;

        setUI();
        loadSharedPreferences();
        setListeners();
        initializeActivity();
    }

    // Functions
    private void setUI() {
        switch_activate = (Switch) this.findViewById(R.id.switch_activate);
        switch_speed_accurate = (Switch) this.findViewById(R.id.switch_speed_accurate);
        et_level_1 = (EditText) this.findViewById(R.id.et_level_1);
        et_level_2 = (EditText) this.findViewById(R.id.et_level_2);
        et_level_3 = (EditText) this.findViewById(R.id.et_level_3);
        et_level_4 = (EditText) this.findViewById(R.id.et_level_4);
        et_level_5 = (EditText) this.findViewById(R.id.et_level_5);
        tv_speed = (TextView) this.findViewById(R.id.tv_speed);
        tv_accuracy_speed = (TextView) this.findViewById(R.id.tv_accuracy_speed);
        btn_save_values = (ImageView) this.findViewById(R.id.btn_save_values);
        btn_delete_values = (ImageView) this.findViewById(R.id.btn_delete_values);

        switch_activate.setChecked(false);
        switch_speed_accurate.setChecked(false);
    }

    private void setListeners() {
        switch_activate.setOnCheckedChangeListener(new OnActivateCheckedChangeListener());
        btn_save_values.setOnClickListener(new OnSaveValuesClickListener());
        btn_delete_values.setOnClickListener(new OnDeleteValuesClickListener());
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

    private void loadSharedPreferences() {
        SharedPreferencesService.initialize(this);
        // Load speed levels
        if(!SharedPreferencesService.getStringItem(speed_level_1_text).equals(SharedPreferencesService.DEFAULT_STRING_VALUE)) {
            et_level_1.setText(SharedPreferencesService.getStringItem(speed_level_1_text));
        }
        if(!SharedPreferencesService.getStringItem(speed_level_2_text).equals(SharedPreferencesService.DEFAULT_STRING_VALUE)) {
            et_level_2.setText(SharedPreferencesService.getStringItem(speed_level_2_text));
        }
        if(!SharedPreferencesService.getStringItem(speed_level_3_text).equals(SharedPreferencesService.DEFAULT_STRING_VALUE)) {
            et_level_3.setText(SharedPreferencesService.getStringItem(speed_level_3_text));
        }
        if(!SharedPreferencesService.getStringItem(speed_level_4_text).equals(SharedPreferencesService.DEFAULT_STRING_VALUE)) {
            et_level_4.setText(SharedPreferencesService.getStringItem(speed_level_4_text));
        }
        if(!SharedPreferencesService.getStringItem(speed_level_5_text).equals(SharedPreferencesService.DEFAULT_STRING_VALUE)) {
            et_level_5.setText(SharedPreferencesService.getStringItem(speed_level_5_text));
        }
        // ---
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

//        For testing
//        speed = (int)(Math.random() * 100) + 0;

        int maxVolume;
        int newVolume = -1;
        int actualVolume;

        if (audioManager.getMode() == AudioManager.MODE_IN_CALL) {
            maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
            actualVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
        } else {
            maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            actualVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
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
            newVolume = (int) (maxVolume * volume_level_max); //TODO: Comprobar ajuste de volumen máximo para reducirlo o no
        }

        //TODO: Añadir ajuste para permitir el registro de logs
        Logger.logOnNote("Speed: " + speed);
        Logger.logOnNote("New Volume: " + newVolume);
        Logger.logOnNote("Actual Volume: " + actualVolume + "\n");

        if (newVolume != -1 && newVolume != actualVolume) {
            if (audioManager.getMode() == AudioManager.MODE_IN_CALL) {
                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, newVolume, 0);
            } else {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            }
        }
    }

    private void setSpeedLevels() {
        speed_level_1 = Integer.valueOf(et_level_1.getText().toString());
        speed_level_2 = Integer.valueOf(et_level_2.getText().toString());
        speed_level_3 = Integer.valueOf(et_level_3.getText().toString());
        speed_level_4 = Integer.valueOf(et_level_4.getText().toString());
        speed_level_5 = Integer.valueOf(et_level_5.getText().toString());
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

    private boolean checkFields() {
        if (et_level_1.getText().length() > 0
                && et_level_2.getText().length() > 0
                && et_level_3.getText().length() > 0
                && et_level_4.getText().length() > 0
                && et_level_5.getText().length() > 0) {
            return true;
        } else {
            return false;
        }
    }
    // ---

    // Listeners
    private class OnActivateCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            if (checkFields()) {
                if (isChecked) {
                    // Activamos el servicio
                    setSpeedLevels();
                    startLocationRequestUpdates();
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

    private class OnSaveValuesClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (checkFields()) {
                // Si los campos están rellenos, se guardan en las SharedPreferences
                HashMap<String, String> items = new HashMap<>();
                items.put(speed_level_1_text, et_level_1.getText().toString());
                items.put(speed_level_2_text, et_level_2.getText().toString());
                items.put(speed_level_3_text, et_level_3.getText().toString());
                items.put(speed_level_4_text, et_level_4.getText().toString());
                items.put(speed_level_5_text, et_level_5.getText().toString());

                Boolean result;
                result = SharedPreferencesService.addStringItems(items);

                if(result) {
                    Toast.makeText(MainActivity.this, "Ajustes guardados", Toast.LENGTH_LONG).show();
                }
            } else {
                showDialog();
            }
        }
    }

    private class OnDeleteValuesClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("¿Eliminar ajustes guardados?");
            alertDialog.setMessage("Para eliminar los niveles de velocidad guardados seleccione CONTINUAR");
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCELAR",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "CONTINUAR",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferencesService.clear();
                            et_level_1.getText().clear();
                            et_level_2.getText().clear();
                            et_level_3.getText().clear();
                            et_level_4.getText().clear();
                            et_level_5.getText().clear();
                            Toast.makeText(MainActivity.this, "Ajustes eliminados", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
    }

    // ---

    // Override
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_menu_settings:
                globalActivity.startActivity(new Intent(globalActivity, SettingsActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }

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
