package com.amaya.smartvolume.data;

import com.amaya.smartvolume.R;
import com.amaya.smartvolume.models.Setting;

public class SettingsData {

    public static String max_volume_setting_id = "setting_max_volume";
    public static String setting_enable_log_id = "setting_enable_log";


    public static Setting max_volume_setting = new Setting(
            max_volume_setting_id,
            "Habilitar volumen máximo",
            "Esto puede dañar los altavoces al alcanzar el volumen al 100%",
            R.drawable.ic_volume,
            false
    );

    public static Setting enable_log_setting = new Setting(
            setting_enable_log_id,
            "Registro de logs",
            "Habilita el registro de logs que se almacenan en SmartVolume/Log/",
            R.drawable.ic_log,
            false
    );

    public static Setting[] getSettings() {

        Setting[] settings = {
                max_volume_setting,
                enable_log_setting
        };
        return settings;
    }

}
