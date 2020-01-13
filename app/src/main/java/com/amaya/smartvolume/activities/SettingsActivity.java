package com.amaya.smartvolume.activities;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amaya.smartvolume.R;
import com.amaya.smartvolume.adapters.SettingsListAdapter;

public class SettingsActivity extends ListActivity {

    private Activity globalActivity;
    private ListView settingsListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setContentView(R.layout.activity_settings);



        globalActivity = this;

        setListView();
        setListeners();
        initializeActivity();
        loadSharedPreferences();
    }

    private void setListView() {
        String[] settingsList = new String[] { "Apple", "Avocado", "Banana",
                "Blueberry", "Coconut", "Durian", "Guava", "Kiwifruit",
                "Jackfruit", "Mango", "Olive", "Pear", "Sugar-apple",  "Apple", "Avocado", "Banana",
                "Blueberry", "Coconut", "Durian", "Guava", "Kiwifruit",
                "Jackfruit", "Mango", "Olive", "Pear", "Sugar-apple" };

        String[] MOBILE_OS =
                new String[] { "Android", "iOS", "WindowsMobile", "Blackberry"};

        setListAdapter(new SettingsListAdapter(this, MOBILE_OS));

        settingsListView = getListView();
        settingsListView.setTextFilterEnabled(true); // ??
    }

    private void setListeners() {
        settingsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                Toast.makeText(getApplicationContext(),
                        ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initializeActivity() {
    }

    private void loadSharedPreferences() {
    }
}
