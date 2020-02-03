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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.amaya.smartvolume.R;
import com.amaya.smartvolume.data.SettingsData;
import com.amaya.smartvolume.services.LocationService;
import com.amaya.smartvolume.services.SharedPreferencesService;
import com.amaya.smartvolume.utils.Logger;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    // Global properties
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final String TAG = "MainActivity";
    public static final String BROADCAST_ACTION = "JOSELITO";
    public static final String LOCATION_EXTRA = "LOCATION_EXTRA";

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
    static Double volume_level_0 = 0.2;
    static Double volume_level_1 = 0.3;
    static Double volume_level_2 = 0.5;
    static Double volume_level_3 = 0.7;
    static Double volume_level_4 = 0.85;
    // ---

    // UI
    private Switch switch_activate;
    private EditText et_level_1;
    private EditText et_level_2;
    private EditText et_level_3;
    private EditText et_level_4;
    private EditText et_level_5;
    static TextView tv_speed;
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
        et_level_1 = (EditText) this.findViewById(R.id.et_level_1);
        et_level_2 = (EditText) this.findViewById(R.id.et_level_2);
        et_level_3 = (EditText) this.findViewById(R.id.et_level_3);
        et_level_4 = (EditText) this.findViewById(R.id.et_level_4);
        et_level_5 = (EditText) this.findViewById(R.id.et_level_5);
        tv_speed = (TextView) this.findViewById(R.id.tv_speed);

        switch_activate.setChecked(false);
    }

    private void setListeners() {
        switch_activate.setOnCheckedChangeListener(new OnActivateCheckedChangeListener());
        et_level_1.addTextChangedListener(new OnTextChangedListener(speed_level_1_text));
        et_level_2.addTextChangedListener(new OnTextChangedListener(speed_level_2_text));
        et_level_3.addTextChangedListener(new OnTextChangedListener(speed_level_3_text));
        et_level_4.addTextChangedListener(new OnTextChangedListener(speed_level_4_text));
        et_level_5.addTextChangedListener(new OnTextChangedListener(speed_level_5_text));
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

    public static void setSpeedText(float speedFloat) {
        String speed = df.format(speedFloat);
        tv_speed.setText(speed);
    }

    private static int getAudioManagerMode() {
        if (audioManager.getMode() == AudioManager.MODE_IN_CALL) {
            return AudioManager.STREAM_VOICE_CALL;
        } else {
            return AudioManager.STREAM_MUSIC;
        }
    }

    private static int getMaxVolume(int maxVolume) {
        Boolean maxVolumeEnable = SharedPreferencesService.getBooleanItem(
                SettingsData.max_volume_setting_id,
                false);
        if(maxVolumeEnable) {
            return maxVolume;
        } else {
            return (int) (maxVolume * volume_level_max);
        }
    }

    private static int getVolumeOfSpeedLevel(int speed, int maxVolume) {
        if (speed == -1) {
            return 0;
        } else if (speed < speed_level_1) {
            return (int) (maxVolume * volume_level_0);
        } else if (speed < speed_level_2) {
            return (int) (maxVolume * volume_level_1);
        } else if (speed < speed_level_3) {
            return (int) (maxVolume * volume_level_2);
        } else if (speed < speed_level_4) {
            return (int) (maxVolume * volume_level_3);
        } else if (speed < speed_level_5) {
            return (int) (maxVolume * volume_level_4);
        } else {
            return getMaxVolume(maxVolume);
        }
    }

    static int nextVolume = -1;

    public static void setVolumeLevel(float speedFloat) {

        int speed = Math.round(speedFloat);

        //TODO: FOR TESTING
//        List<Integer> speeds = new ArrayList<>();
//        speeds.add(24);
//        speeds.add(31);
//        speeds.add(34);
//        speeds.add(39);
//        speeds.add(42);
//        speeds.add(41);
//        Random random = new Random();
//        int index = random.nextInt(speeds.size());
//        speed = speeds.get(index);
        // ---

        int audioManagerMode = getAudioManagerMode();
        int maxVolume = audioManager.getStreamMaxVolume(audioManagerMode);
        int actualVolume = audioManager.getStreamVolume(audioManagerMode);
        int newVolume = getVolumeOfSpeedLevel(speed, maxVolume);

        Logger.logOnNote("setting.. Speed: " + speed);
        Logger.logOnNote("setting.. New Vol: " + newVolume);
        Logger.logOnNote("setting.. Next Vol: " + nextVolume+ "\n");

        if (nextVolume != -1 && newVolume == nextVolume) {
            Logger.logOnNote("-> CASO B: next = -1"+ "\n");
            nextVolume = -1;
            // Cambiar volumen
            if (newVolume != actualVolume) {
                audioManager.setStreamVolume(audioManagerMode, newVolume, 0);
            }
        } else {
            Logger.logOnNote("-> CASO A: next = new"+ "\n");
            nextVolume = newVolume;
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
                    setSpeedText(0);
                }
            } else {
                showDialog();
                switch_activate.setChecked(false);
            }
        }
    }

    private class OnTextChangedListener implements TextWatcher {

        private String speed_level_text;
        public OnTextChangedListener(String speed_level_text) {
            this.speed_level_text = speed_level_text;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence text, int start,
                                  int before, int count) {
            if(text.length() != 0) {
                SharedPreferencesService.addStringItem(speed_level_text, text.toString());
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    private void deleteSettings() {
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
                            Toast.makeText(MainActivity.this, "Ajustes reestablecidos", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
    // ---

    // Override
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_menu_help:
                globalActivity.startActivity(new Intent(globalActivity, HelpActivity.class));
                break;
            case R.id.item_menu_settings:
                globalActivity.startActivity(new Intent(globalActivity, SettingsActivity.class));
                break;
            case R.id.item_menu_delete_settings:
                deleteSettings();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
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
            float speed = intent.getFloatExtra(LOCATION_EXTRA, -1);
            if (speed != -1) {
                setSpeedText(speed);
                setVolumeLevel(speed);
            }
        }
    }
    // ---
}
