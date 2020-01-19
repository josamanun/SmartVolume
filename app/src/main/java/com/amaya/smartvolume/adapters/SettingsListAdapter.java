package com.amaya.smartvolume.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.amaya.smartvolume.R;
import com.amaya.smartvolume.models.Setting;
import com.amaya.smartvolume.services.SharedPreferencesService;

public class SettingsListAdapter extends ArrayAdapter<Setting> {

    private final Context globalContext;
    private final Setting[] values;
    private Setting actual_setting;

    private ImageView iv_setting_icon;
    private TextView tv_setting_title;
    private TextView tv_setting_subhead;
    private Switch switch_setting;

    public SettingsListAdapter(Context context, Setting[] values) {
        super(context, R.layout.settings_item_list, values);
        this.globalContext = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) globalContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.settings_item_list, parent, false);

        actual_setting = values[position];
        setUI(rowView);
        setContentUI();
        setListeners(actual_setting);

        return rowView;
    }

    private void setUI(View rowView) {
        iv_setting_icon = (ImageView) rowView.findViewById(R.id.iv_setting_icon);
        tv_setting_title = (TextView) rowView.findViewById(R.id.tv_setting_title);
        tv_setting_subhead = (TextView) rowView.findViewById(R.id.tv_setting_subhead);
        switch_setting = (Switch) rowView.findViewById(R.id.switch_setting);
    }

    private void setContentUI() {
        iv_setting_icon.setImageResource(actual_setting.getIcon());
        tv_setting_title.setText(actual_setting.getTitle());
        tv_setting_subhead.setText(actual_setting.getSubhead());
        switch_setting.setChecked(
                SharedPreferencesService.getBooleanItem(actual_setting.getId(),
                actual_setting.getDefaultCheck()));
    }

    private void setListeners(Setting actual_setting) {
        switch_setting.setOnCheckedChangeListener(new OnSwitchChangeListener(actual_setting));

    }

    private class OnSwitchChangeListener implements CompoundButton.OnCheckedChangeListener {

        private Setting actual_setting;

        public OnSwitchChangeListener(Setting actual_setting) {
            this.actual_setting = actual_setting;
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
            SharedPreferencesService.addBooleanItem(actual_setting.getId(), checked);
        }
    }
}
