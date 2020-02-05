package com.amaya.smartvolume.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.amaya.smartvolume.R;
import com.amaya.smartvolume.data.SettingsData;
import com.amaya.smartvolume.models.Setting;
import com.amaya.smartvolume.services.SharedPreferencesService;
import com.amaya.smartvolume.utils.VersionInfo;

import static com.amaya.smartvolume.fragments.HomeFragment.et_level_1;
import static com.amaya.smartvolume.fragments.HomeFragment.et_level_2;
import static com.amaya.smartvolume.fragments.HomeFragment.et_level_3;
import static com.amaya.smartvolume.fragments.HomeFragment.et_level_4;
import static com.amaya.smartvolume.fragments.HomeFragment.et_level_5;

public class SettingsListAdapter extends ArrayAdapter<Setting> {

    private final Context globalContext;
    private final Setting[] values;
    private Setting actual_setting;

    private TextView tv_item_version;

    private ImageView iv_setting_icon;
    private TextView tv_setting_title;
    private TextView tv_setting_subhead;
    private Switch switch_setting;
    private Spinner spinner_setting;
    private LinearLayout ll_spinner_setting;
    private LinearLayout ll_switch_setting;
    private TextView tv_setting_restore;

    public SettingsListAdapter(Context context, Setting[] values) {
        super(context, R.layout.settings_item_list, values);
        this.globalContext = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) globalContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        actual_setting = values[position];

        if (actual_setting.isVersion()) {
            View rowView = inflater.inflate(R.layout.version_item_list, parent, false);
            setVersionUI(rowView);
            return rowView;
        }

        if (actual_setting.isRestore()) {
            View rowView = inflater.inflate(R.layout.restore_setting_item_list, parent, false);
            setRestoreUI(rowView);
            return rowView;
        }
        View rowView = inflater.inflate(R.layout.settings_item_list, parent, false);

        setUI(rowView);
        setContentUI();
        setListeners(actual_setting);

        return rowView;
    }

    private void setRestoreUI(View rowView) {
        tv_setting_restore = (TextView) rowView.findViewById(R.id.tv_setting_restore);
        tv_setting_restore.setText(actual_setting.getTitle());
        tv_setting_restore.setPaintFlags(tv_setting_restore.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        tv_setting_restore.setOnClickListener(new OnRestoreClickListener());
    }

    private void setVersionUI(View rowView) {
        tv_item_version = (TextView) rowView.findViewById(R.id.tv_item_version);
        tv_item_version.setText(VersionInfo.getVersionCode(globalContext));
    }

    private void setUI(View rowView) {;
        iv_setting_icon = (ImageView) rowView.findViewById(R.id.iv_setting_icon);
        tv_setting_title = (TextView) rowView.findViewById(R.id.tv_setting_title);
        tv_setting_subhead = (TextView) rowView.findViewById(R.id.tv_setting_subhead);
        switch_setting = (Switch) rowView.findViewById(R.id.switch_setting);
        spinner_setting = (Spinner) rowView.findViewById(R.id.spinner_setting);
        ll_spinner_setting = (LinearLayout) rowView.findViewById(R.id.ll_spinner_setting);
        ll_switch_setting = (LinearLayout) rowView.findViewById(R.id.ll_switch_setting);
    }

    private void setContentUI() {
        iv_setting_icon.setImageResource(actual_setting.getIcon());
        tv_setting_title.setText(actual_setting.getTitle());
        tv_setting_subhead.setText(actual_setting.getSubhead());

        switch (actual_setting.getLayout()) {
            case SettingsData.SWITCH_LAYOUT:
                loadSwitch();
                showSwitch();
                break;
            case SettingsData.SPINNER_LAYOUT:
                loadSpinner();
                showSpinner();
                break;
            default:
                break;
        }
    }

    private void loadSwitch() {
        switch_setting.setChecked(
                SharedPreferencesService.getBooleanItem(actual_setting.getId(),
                        actual_setting.getDefaultCheck()));
        switch_setting.setOnCheckedChangeListener(new OnSwitchChangeListener(actual_setting));
    }

    private void loadSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this.globalContext,
                R.layout.spinner_item,
                actual_setting.getSpinnerItems());

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        spinner_setting.setAdapter(adapter);
        spinner_setting.setSelection(
                SharedPreferencesService.getIntegerItem(
                        actual_setting.getId(),
                        SettingsData.DEFAULT_REFRESH_LOCATION_INDEX
                )
        );
        spinner_setting.setOnItemSelectedListener(new OnSpinnerItemSelectedListener());
    }

    private void setListeners(Setting actual_setting) {
        switch_setting.setOnCheckedChangeListener(new OnSwitchChangeListener(actual_setting));

    }

    private void showSwitch() {
        ll_switch_setting.setVisibility(View.VISIBLE);
        ll_spinner_setting.setVisibility(View.GONE);
    }

    private void showSpinner() {
        ll_spinner_setting.setVisibility(View.VISIBLE);
        ll_switch_setting.setVisibility(View.GONE);
    }

    private void deleteSettings() {
        AlertDialog alertDialog = new AlertDialog.Builder(globalContext).create();
        alertDialog.setTitle("¿Restablecer ajustes?");
        alertDialog.setMessage("Para establecer los ajustes por defecto seleccione CONTINUAR");
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
                        notifyDataSetChanged();
                        Toast.makeText(globalContext, "Ajustes restablecidos", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
    // ---

    // Listeners
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

    private class OnSpinnerItemSelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            SharedPreferencesService.addIntegerItem(actual_setting.getId(), pos);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    private class OnRestoreClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            deleteSettings();
        }
    }
    // ---
}
