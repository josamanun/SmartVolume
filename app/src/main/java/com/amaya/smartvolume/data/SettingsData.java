package com.amaya.smartvolume.data;

import com.amaya.smartvolume.R;
import com.amaya.smartvolume.models.Setting;

import java.util.Arrays;
import java.util.List;

public class SettingsData {

    public static String max_volume_setting_id = "setting_max_volume";
    public static String setting_enable_log_id = "setting_enable_log";
    public static String refresh_location_setting_id = "refresh_location_setting";

    public static final int SWITCH_LAYOUT = R.id.switch_setting;
    public static final int SPINNER_LAYOUT = R.id.spinner_setting;

    public static List<String> refreshLocationSegOptions = Arrays.asList("0.1","0.2","0.3","0.4","0.5","0.6","0.7","0.8","0.9","1.0","1.2","1.5","2.0");
    public static long DEFAULT_REFRESH_FREQUENCY = 500;
    public static int DEFAULT_REFRESH_LOCATION_INDEX = 4;

    public static Setting[] getSettings() {

        Setting[] settings = {
                max_volume_setting,
                enable_log_setting,
                refresh_location_setting,
                version_setting // Version always last
        };
        return settings;
    }

    public static Setting version_setting = new Setting(true);

    public static Setting max_volume_setting = new Setting(
            max_volume_setting_id,
            "Habilitar volumen máximo",
            "Esto puede dañar los altavoces al alcanzar el volumen al 100%.",
            R.drawable.ic_volume,
            false,
            SWITCH_LAYOUT
    );

    public static Setting enable_log_setting = new Setting(
            setting_enable_log_id,
            "Registro de logs",
            "Habilita el registro de logs que se almacenan en SmartVolume/Log/.",
            R.drawable.ic_log,
            false,
            SWITCH_LAYOUT
    );


    public static Setting refresh_location_setting = new Setting(
            refresh_location_setting_id ,
            "Frecuencia de localización",
            "Puede alterar la precisión de la velocidad. Unidad: segundos.",
            R.drawable.ic_refesh,
            false,
            SPINNER_LAYOUT,
            refreshLocationSegOptions
    );





}
