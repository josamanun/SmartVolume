package com.amaya.smartvolume.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.amaya.smartvolume.R;
import com.amaya.smartvolume.activities.HelpActivity;
import com.amaya.smartvolume.data.SettingsData;
import com.amaya.smartvolume.services.LocationService;
import com.amaya.smartvolume.services.SharedPreferencesService;
import com.amaya.smartvolume.utils.Logger;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import static com.amaya.smartvolume.activities.MainActivity.MY_PERMISSIONS_REQUEST_LOCATION;
import static com.amaya.smartvolume.data.DataGenerator.getSpeedLevel1;
import static com.amaya.smartvolume.data.DataGenerator.getSpeedLevel2;
import static com.amaya.smartvolume.data.DataGenerator.getSpeedLevel3;
import static com.amaya.smartvolume.data.DataGenerator.getSpeedLevel4;
import static com.amaya.smartvolume.data.DataGenerator.getSpeedLevel5;
import static com.amaya.smartvolume.data.SettingsData.DEFAULT_CHECKED_WELCOME_DIALOG;

public class HomeFragment extends Fragment {

    // Global properties
    public static final String TAG = "HomeFragment";
    public static final String BROADCAST_ACTION = "JOSELITO";
    public static final String LOCATION_EXTRA = "LOCATION_EXTRA";

    static Context globalContext;
    static FragmentActivity globalFragmentActivity;

    public static String ACTIVATE_TEXT = "Encencido";
    public static String DEACTIVATE_TEXT= "Apagado";

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

    static Intent locationIntent;
    BroadcastReceiver locationRefreshReceiver;
    static AudioManager audioManager;

    // UI
    public static ToggleButton tb_activate;
    public static TextView tv_activate;
    private LinearLayout ll_help;
    private AdView mAdView;

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
        SharedPreferencesService.initialize(globalContext);
        setListeners();
        initializeFragment();
        initializeAdBanner();
        showWelcomeDialogIfFirstTime();
        return view;
    }

    private void setUI(View view) {
        tb_activate = (ToggleButton) view.findViewById(R.id.tb_activate);
        tv_activate = (TextView) view.findViewById(R.id.tv_activate);
        ll_help = (LinearLayout) view.findViewById(R.id.ll_help);
        mAdView = (AdView) view.findViewById(R.id.adView);

        tb_activate.setChecked(false);
        setDeactivateText();
    }

    private void setListeners() {
        tb_activate.setOnCheckedChangeListener(new OnActivateCheckedChangeListener());
        ll_help.setOnClickListener(new OnHelpClickListener());
    }

    private void initializeFragment() {
        locationRefreshReceiver = new LocationRefreshReceiver();
        globalContext.registerReceiver(locationRefreshReceiver, new IntentFilter(BROADCAST_ACTION));
        audioManager = (AudioManager) globalFragmentActivity.getSystemService(Context.AUDIO_SERVICE);
        locationIntent = new Intent(globalContext, LocationService.class);
    }


    private void initializeAdBanner() {
        MobileAds.initialize(globalFragmentActivity, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    // Listeners
    private class OnActivateCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // Comprobamos los permisos
            if (ActivityCompat.checkSelfPermission(globalContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(globalContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                checkLocationPermission();
                tb_activate.setChecked(false);
                setDeactivateText();
            } else {
                if (isChecked) {
                    setSpeedLevels();
                    if (checkSpeedOrder()) {
                        // Reiniciamos el contador de muteo
                        com.amaya.smartvolume.utils.AudioManager.resetMuteTimer();
                        // Activamos el servicio
                        startLocationRequestUpdates();
                        setActivateText();
                        Toast.makeText(globalContext, "GPS activado", Toast.LENGTH_SHORT).show();
                    } else {
                        showDialog();
                        tb_activate.setChecked(false);
                        setDeactivateText();
                    }
                } else {
                    // Desactivamos el servicio
                    stopLocationRequestUpdates();
                    setDeactivateText();
                    Toast.makeText(globalContext, "GPS desactivado", Toast.LENGTH_SHORT).show();
                }
            }
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
    private boolean checkSpeedOrder() {
        if (speed_level_1 < speed_level_2
            && speed_level_2 < speed_level_3
            && speed_level_3 < speed_level_4
            && speed_level_4 < speed_level_5
        ) {
            return true;
        } else {
            return false;
        }
    }

    private void setSpeedLevels() {
        speed_level_1 = getSpeedLevel1();
        speed_level_2 = getSpeedLevel2();
        speed_level_3 = getSpeedLevel3();
        speed_level_4 = getSpeedLevel4();
        speed_level_5 = getSpeedLevel5();
    }

    public static void startLocationRequestUpdates() {
        locationIntent.putExtra(speed_level_1_text, speed_level_1);
        locationIntent.putExtra(speed_level_2_text, speed_level_2);
        locationIntent.putExtra(speed_level_3_text, speed_level_3);
        locationIntent.putExtra(speed_level_4_text, speed_level_4);
        locationIntent.putExtra(speed_level_5_text, speed_level_5);

        // Start foreground location service
        ContextCompat.startForegroundService(globalContext, locationIntent);

        tb_activate.setChecked(true);
        setActivateText();
        Toast.makeText(globalContext, "GPS activado", Toast.LENGTH_SHORT).show();
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

    private void showWelcomeDialogIfFirstTime() {
        Boolean alreadyChecked = SharedPreferencesService.getBooleanItem(SettingsData.show_welcome_dialog_id, DEFAULT_CHECKED_WELCOME_DIALOG);
        if(!alreadyChecked) {
            // custom dialog
            final Dialog dialog = new Dialog(globalContext);
            dialog.setContentView(R.layout.welcome_dialog);
            dialog.setTitle("¡Bienvenido!");

            CheckBox checkboxWelcome = (CheckBox) dialog.findViewById(R.id.checkbox_welcome);
            TextView tv_close_welcome_dialog = (TextView) dialog.findViewById(R.id.tv_close_welcome_dialog);

            checkboxWelcome.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    Log.i(TAG, "onCheckedChanged: ");
                    SharedPreferencesService.addBooleanItem(SettingsData.show_welcome_dialog_id, checked);
                }
            });

            tv_close_welcome_dialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    private void showDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(globalContext).create();
        alertDialog.setTitle("Comprueba los niveles de volumen");
        alertDialog.setMessage("Los niveles de volumen deben establecerse de menor a mayor velocidad en Ajustes.");
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
        int audioManagerMode = com.amaya.smartvolume.utils.
                AudioManager.getAudioManagerMode(audioManager);
        int maxVolume = audioManager.getStreamMaxVolume(audioManagerMode);
        int actualVolume = audioManager.getStreamVolume(audioManagerMode);
        int newVolume = com.amaya.smartvolume.utils.
                AudioManager.getVolumeOfSpeedLevel(speed, maxVolume);

        Logger.logOnNote("Speed: " + speed);
        Logger.logOnNote("New Vol: " + newVolume);
        Logger.logOnNote("Next Vol: " + nextVolume);

        if (nextVolume != -1 && newVolume == nextVolume) {
            Logger.logOnNote("-> Volumen verificado, reiniciado nextVolume" + "\n");
            nextVolume = -1;
            // Cambiar volumen
            if (newVolume != actualVolume) {
                audioManager.setStreamVolume(audioManagerMode, newVolume, 0);
            }
        } else {
            Logger.logOnNote("-> Nuevo volumen, actualizado nextVolume " + "\n");
            nextVolume = newVolume;
        }
    }

    public static void setActivateText() {
        tv_activate.setText(ACTIVATE_TEXT);
        tv_activate.setTextColor(globalContext.getColor(R.color.colorPrimary));
    }
    public static void setDeactivateText() {
        tv_activate.setText(DEACTIVATE_TEXT);
        tv_activate.setTextColor(globalContext.getColor(R.color.colorGrey));
    }
    // ---

    // Location receiver
    private class LocationRefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            float speed = intent.getFloatExtra(LOCATION_EXTRA, -1);
            if (speed != -1) {
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
                    tb_activate.setChecked(true);
                    setActivateText();
                    Toast.makeText(globalContext, "GPS activado", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(globalContext, "Es necesario aceptar los permisos", Toast.LENGTH_LONG).show();
                    tb_activate.setChecked(false);
                    setDeactivateText();
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
