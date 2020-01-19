package com.amaya.smartvolume.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.amaya.smartvolume.R;
import com.amaya.smartvolume.adapters.SettingsListAdapter;
import com.amaya.smartvolume.data.SettingsData;

public class SettingsActivity extends Activity {

    private static String TAG = "SettingsActivity";

    private Activity globalActivity;
    private ListView lv_settings;
    private LinearLayout ll_setting_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        globalActivity = this;

        setUI();
        setListView();
        setListeners();
    }

    private void setUI() {
        ll_setting_back = (LinearLayout) this.findViewById(R.id.ll_setting_back);
        lv_settings = (ListView) this.findViewById(R.id.lv_settings);
    }

    private void setListView() {
        lv_settings.setAdapter(new SettingsListAdapter(this, SettingsData.getSettings()));
    }

    private void setListeners() {
        ll_setting_back.setOnClickListener(new OnBackClickListener());
        lv_settings.setClickable(true);
    }

    private class OnBackClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            globalActivity.finish();
        }
    }
}
