package com.amaya.smartvolume.activities;

import android.app.Activity;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.amaya.smartvolume.R;
import com.amaya.smartvolume.fragments.HomeFragment;
import com.amaya.smartvolume.fragments.SettingsFragment;
import com.amaya.smartvolume.utils.FragmentManager;

public class MainActivity extends AppCompatActivity {

    // Global properties
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