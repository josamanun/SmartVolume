package com.amaya.smartvolume.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.amaya.smartvolume.R;
import com.amaya.smartvolume.activities.HelpActivity;
import com.amaya.smartvolume.services.LocationService;
import com.amaya.smartvolume.services.SharedPreferencesService;
import com.amaya.smartvolume.utils.Logger;

import java.text.DecimalFormat;

public class HomeFragment extends Fragment {

    // Global properties
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final String BROADCAST_ACTION = "JOSELITO";
    public static final String LOCATION_EXTRA = "LOCATION_EXTRA";

    static Context globalContext;
    static FragmentActivity globalFragmentActivity;

    public static String speed_level_1_text = "speed_level_1";
    public static String speed_level_2_text= "speed_level_2";
    public static String speed_level_3_text = "speed_level_3";
    public static String speed_level_4_text = "speed_level_4";
    public static String speed_level_5_text = "speed_level_5";

    public static Integer speed_level_1 = 0;
    public static Integer speed_level_2 = 0;
    public static Integer speed_level_3 = 0;
    public static Integer speed_level_4 = 0;
    public static Integer speed_level_5 = 0;

    static DecimalFormat df;
    static DecimalFormat dfAccuracy;

    static Intent locationIntent;
    BroadcastReceiver locationRefreshReceiver;
    static AudioManager audioManager;

    // UI
    private Switch switch_activate;
    public static EditText et_level_1;
    public static EditText et_level_2;
    public static EditText et_level_3;
    public static EditText et_level_4;
    public static EditText et_level_5;
    static TextView tv_speed;
    private LinearLayout ll_help;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        globalContext = this.getContext();
        globalFragmentActivity = this.getActivity();

        setUI(view);
        loadSharedPreferences();
        setListeners();
        initializeFragment();

        return view;
    }

    private void setUI(View view) {
        switch_activate = (Switch) view.findViewById(R.id.switch_activate);
        et_level_1 = (EditText) view.findViewById(R.id.et_level_1);
        et_level_2 = (EditText) view.findViewById(R.id.et_level_2);
        et_level_3 = (EditText) view.findViewById(R.id.et_level_3);
        et_level_4 = (EditText) view.findViewById(R.id.et_level_4);
        et_level_5 = (EditText) view.findViewById(R.id.et_level_5);
        tv_speed = (TextView) view.findViewById(R.id.tv_speed);
        ll_help = (LinearLayout) view.findViewById(R.id.ll_help);

        switch_activate.setChecked(false);
    }

    private void loadSharedPreferences() {
        SharedPreferencesService.initialize(globalContext);
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

    private void setListeners() {
        switch_activate.setOnCheckedChangeListener(new OnActivateCheckedChangeListener());
        et_level_1.addTextChangedListener(new OnTextChangedListener(speed_level_1_text));
        et_level_2.addTextChangedListener(new OnTextChangedListener(speed_level_2_text));
        et_level_3.addTextChangedListener(new OnTextChangedListener(speed_level_3_text));
        et_level_4.addTextChangedListener(new OnTextChangedListener(speed_level_4_text));
        et_level_5.addTextChangedListener(new OnTextChangedListener(speed_level_5_text));
        ll_help.setOnClickListener(new OnHelpClickListener());
    }

    private void initializeFragment() {
        locationRefreshReceiver = new LocationRefreshReceiver();
        globalContext.registerReceiver(locationRefreshReceiver, new IntentFilter(BROADCAST_ACTION));
        audioManager = (AudioManager) globalFragmentActivity.getSystemService(Context.AUDIO_SERVICE);

        locationIntent = new Intent(globalContext, LocationService.class);
        // Utils
        df = new DecimalFormat("#");
        dfAccuracy = new DecimalFormat("#.##");
    }

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

    private class OnHelpClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            globalContext.startActivity(new Intent(globalFragmentActivity, HelpActivity.class));
        }
    }
    // ---

    // Functions
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

    private void setSpeedLevels() {
        speed_level_1 = Integer.valueOf(et_level_1.getText().toString());
        speed_level_2 = Integer.valueOf(et_level_2.getText().toString());
        speed_level_3 = Integer.valueOf(et_level_3.getText().toString());
        speed_level_4 = Integer.valueOf(et_level_4.getText().toString());
        speed_level_5 = Integer.valueOf(et_level_5.getText().toString());
    }

    public static void setSpeedText(float speedFloat) {
        String speed = df.format(speedFloat);
        tv_speed.setText(speed);
    }

    private void startLocationRequestUpdates() {
        locationIntent.putExtra(speed_level_1_text, speed_level_1);
        locationIntent.putExtra(speed_level_2_text, speed_level_2);
        locationIntent.putExtra(speed_level_3_text, speed_level_3);
        locationIntent.putExtra(speed_level_4_text, speed_level_4);
        locationIntent.putExtra(speed_level_5_text, speed_level_5);

        // Start foreground location service
        ContextCompat.startForegroundService(globalContext, locationIntent);
    }

    private void stopLocationRequestUpdates() {
        globalContext.stopService(locationIntent);
    }

    public static void checkLocationPermission() {
        // First stop location service
        globalContext.stopService(locationIntent);

        globalFragmentActivity.requestPermissions(
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                },
                MY_PERMISSIONS_REQUEST_LOCATION);
    }

    private void showDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(globalContext).create();
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

        int audioManagerMode = com.amaya.smartvolume.utils.
                AudioManager.getAudioManagerMode(audioManager);
        int maxVolume = audioManager.getStreamMaxVolume(audioManagerMode);
        int actualVolume = audioManager.getStreamVolume(audioManagerMode);
        int newVolume = com.amaya.smartvolume.utils.
                AudioManager.getVolumeOfSpeedLevel(speed, maxVolume);

        Logger.logOnNote("Speed: " + speed);
        Logger.logOnNote("New Vol: " + newVolume);
        Logger.logOnNote("Next Vol: " + nextVolume + "\n");

        if (nextVolume != -1 && newVolume == nextVolume) {
            Logger.logOnNote("-> CASO B: next = -1" + "\n");
            nextVolume = -1;
            // Cambiar volumen
            if (newVolume != actualVolume) {
                audioManager.setStreamVolume(audioManagerMode, newVolume, 0);
            }
        } else {
            Logger.logOnNote("-> CASO A: next = new" + "\n");
            nextVolume = newVolume;
        }
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


    // Overrides
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationRequestUpdates();
                } else {
                    Toast.makeText(globalContext, "Es necesario aceptar los permisos", Toast.LENGTH_LONG).show();
                    switch_activate.setChecked(false);
                }
                return;
            }

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onDestroy() {
        stopLocationRequestUpdates();
        super.onDestroy();
    }
    // ---
}
