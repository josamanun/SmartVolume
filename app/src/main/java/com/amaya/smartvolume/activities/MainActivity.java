package com.amaya.smartvolume.activities;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.amaya.smartvolume.R;
import com.amaya.smartvolume.fragments.HomeFragment;
import com.amaya.smartvolume.fragments.SettingsFragment;
import com.amaya.smartvolume.utils.FragmentManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import static com.amaya.smartvolume.adapters.SettingsListAdapter.setLogSettingChecked;
import static com.amaya.smartvolume.fragments.HomeFragment.setDeactivateText;
import static com.amaya.smartvolume.fragments.HomeFragment.startLocationRequestUpdates;
import static com.amaya.smartvolume.fragments.HomeFragment.tb_activate;

public class MainActivity extends AppCompatActivity {

    // Global properties
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final int MY_PERMISSIONS_WRITE_EXTERNAL = 98;
    public static final String TAG = "MainActivity";

    public static String HOME_FRAGMENT_TAG = "HOME_FRAGMENT_TAG";
    public static String SETTINGS_FRAGMENT_TAG = "SETTINGS_FRAGMENT_TAG";
    public static Integer CONTENT_ID = R.id.frame_container;

    static Activity globalActivity;
    static FragmentActivity globalFragmentActivity ;
    // ---

    // UI
    private BottomNavigationView navigation_view;
    // ---

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        globalActivity = this;
        globalFragmentActivity = this;

        setUI();
        setListeners();
        setFragmentByDefault();
    }

    // Functions
    private void setUI() {
        navigation_view = (BottomNavigationView) findViewById(R.id.navigation);
    }

    private void setListeners() {
        navigation_view.setOnNavigationItemSelectedListener(new OnNavigationItemClickListener());
    }

    public void setFragmentByDefault() {
        navigation_view.getMenu().getItem(0).setChecked(true);
        FragmentManager.addFragmentActivity(
                globalFragmentActivity,
                new HomeFragment(),
                HOME_FRAGMENT_TAG,
                CONTENT_ID);
    }

    public static void checkStoragePermission() {
        globalActivity.requestPermissions(
                new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                },
                MY_PERMISSIONS_WRITE_EXTERNAL);
    }
    // ---

    // Listeners
    private class OnNavigationItemClickListener implements BottomNavigationView.OnNavigationItemSelectedListener {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.item_menu_home:
                    FragmentManager.changeBetweenFragment(
                            globalFragmentActivity,
                            SETTINGS_FRAGMENT_TAG,
                            HOME_FRAGMENT_TAG,
                            new HomeFragment(),
                            CONTENT_ID
                    );
                    return true;
                case R.id.item_menu_settings:
                    FragmentManager.changeBetweenFragment(
                            globalFragmentActivity,
                            HOME_FRAGMENT_TAG,
                            SETTINGS_FRAGMENT_TAG,
                            new SettingsFragment(),
                            CONTENT_ID
                    );
                    return true;
                default:
                    return false;
            }
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
                    Toast.makeText(this, "Es necesario aceptar los permisos de GPS", Toast.LENGTH_LONG).show();
                    tb_activate.setChecked(false);
                    setDeactivateText();
                }
                return;
            }
            case MY_PERMISSIONS_WRITE_EXTERNAL: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setLogSettingChecked();
                } else {
                    Toast.makeText(this, "Es necesario aceptar los permisos de escritura", Toast.LENGTH_LONG).show();
                }
                return;
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        Fragment settingsFragment = getSupportFragmentManager().findFragmentByTag(SETTINGS_FRAGMENT_TAG);
        // Si la vista activada es Ajustes, volvemos a Principal
        if (settingsFragment != null && settingsFragment.isVisible()) {
            FragmentManager.changeBetweenFragment(
                    globalFragmentActivity,
                    SETTINGS_FRAGMENT_TAG,
                    HOME_FRAGMENT_TAG,
                    new HomeFragment(),
                    CONTENT_ID);
            navigation_view.getMenu().getItem(0).setChecked(true);
            return;
        }

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Vuelve a pulsar ATRÁS para salir", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
    // ---
}