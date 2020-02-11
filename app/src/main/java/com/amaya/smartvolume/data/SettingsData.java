package com.amaya.smartvolume.data;

import com.amaya.smartvolume.R;
import com.amaya.smartvolume.models.Setting;

import static com.amaya.smartvolume.data.DataGenerator.getIndexOfDefaultFrequency;
import static com.amaya.smartvolume.data.DataGenerator.getIndexOfDefaultSpeedLevel1;
import static com.amaya.smartvolume.data.DataGenerator.getIndexOfDefaultSpeedLevel2;
import static com.amaya.smartvolume.data.DataGenerator.getIndexOfDefaultSpeedLevel3;
import static com.amaya.smartvolume.data.DataGenerator.getIndexOfDefaultSpeedLevel4;
import static com.amaya.smartvolume.data.DataGenerator.getIndexOfDefaultSpeedLevel5;
import static com.amaya.smartvolume.data.DataGenerator.refreshLocationSegOptions;
import static com.amaya.smartvolume.data.DataGenerator.speedOptions;

public class SettingsData {

    public static final String SETTING_HEADER_TYPE = "SETTING_HEADER_TYPE";
    public static final String SETTING_RESTORE_TYPE = "SETTING_RESTORE_TYPE ";
    public static final String SETTING_VERSION_TYPE = "SETTING_VERSION_TYPE ";

    public static String speed_level_1_id = "speed_level_1";
    public static String speed_level_2_id = "speed_level_2";
    public static String speed_level_3_id = "speed_level_3";
    public static String speed_level_4_id = "speed_level_4";
    public static String speed_level_5_id = "speed_level_5";
    public static String max_volume_setting_id = "setting_max_volume";
    public static final String setting_enable_log_id = "setting_enable_log";
    public static String refresh_frequency_setting_id = "refresh_frequency_setting ";

    public static final int ICON_LAYOUT = R.id.iv_setting_icon;
    public static final int TEXT_LAYOUT = R.id.tv_setting_text;
    public static final int SWITCH_LAYOUT = R.id.switch_setting;
    public static final int SPINNER_LAYOUT = R.id.spinner_setting;

    public static final int DEFAULT_REFRESH_LOCATION_INDEX = getIndexOfDefaultFrequency; // Default: 0.5 seg
    public static final int DEFAULT_SPEED_LEVEL_1_INDEX = getIndexOfDefaultSpeedLevel1; // Default: 20 kmh
    public static final int DEFAULT_SPEED_LEVEL_2_INDEX = getIndexOfDefaultSpeedLevel2; // Default: 40 kmh
    public static final int DEFAULT_SPEED_LEVEL_3_INDEX = getIndexOfDefaultSpeedLevel3; // Default: 60 kmh
    public static final int DEFAULT_SPEED_LEVEL_4_INDEX = getIndexOfDefaultSpeedLevel4; // Default: 80 kmh
    public static final int DEFAULT_SPEED_LEVEL_5_INDEX = getIndexOfDefaultSpeedLevel5; // Default: 100 kmh

    public static Setting[] getSettings() {
        Setting[] settings = {
                header_volume_setting,
                speed_level_1,
                speed_level_2,
                speed_level_3,
                speed_level_4,
                speed_level_5,
                header_other_setting,
                max_volume_setting,
                enable_log_setting,
                refresh_location_setting,
                restore_setting,
                version_setting // Version always last
        };
        return settings;
    }

    public static Setting header_volume_setting  = new Setting(SETTING_HEADER_TYPE, "NIVELES DE VOLUMEN");
    public static Setting restore_setting  = new Setting(SETTING_RESTORE_TYPE, "Restablecer ajustes");
    public static Setting version_setting = new Setting(SETTING_VERSION_TYPE, "");

    public static Setting speed_level_1 = new Setting(
            speed_level_1_id,
            "Volumen al 30%",
            "Velocidad para subir el volumen al 30%.",
            "30%",
            TEXT_LAYOUT,
            SPINNER_LAYOUT,
            speedOptions,
            DEFAULT_SPEED_LEVEL_1_INDEX
    );

    public static Setting speed_level_2 = new Setting(
            speed_level_2_id,
            "Volumen al 50%",
            "Velocidad para subir el volumen al 50%.",
            "50%",
            TEXT_LAYOUT,
            SPINNER_LAYOUT,
            speedOptions,
            DEFAULT_SPEED_LEVEL_2_INDEX
    );

    public static Setting speed_level_3 = new Setting(
            speed_level_3_id,
            "Volumen al 70%",
            "Velocidad para subir el volumen al 70%.",
            "70%",
            TEXT_LAYOUT,
            SPINNER_LAYOUT,
            speedOptions,
            DEFAULT_SPEED_LEVEL_3_INDEX
    );

    public static Setting speed_level_4 = new Setting(
            speed_level_4_id,
            "Volumen al 85%",
            "Velocidad para subir el volumen al 85%.",
            "85%",
            TEXT_LAYOUT,
            SPINNER_LAYOUT,
            speedOptions,
            DEFAULT_SPEED_LEVEL_4_INDEX
    );

    public static Setting speed_level_5 = new Setting(
            speed_level_5_id,
            "Volumen al 95%",
            "Velocidad para subir el volumen al 95%.",
            "95%",
            TEXT_LAYOUT,
            SPINNER_LAYOUT,
            speedOptions,
            DEFAULT_SPEED_LEVEL_5_INDEX
    );

    public static Setting header_other_setting  = new Setting(SETTING_HEADER_TYPE, "OTROS AJUSTES");

    public static Setting max_volume_setting = new Setting(
            max_volume_setting_id,
            "Habilitar volumen al 100%",
            "Alcanzar el volumen máximo puede dañar los altavoces.",
            R.drawable.ic_volume,
            false,
            ICON_LAYOUT,
            SWITCH_LAYOUT
    );

    public static Setting enable_log_setting = new Setting(
            setting_enable_log_id,
            "Registro de logs",
            "Habilita el registro de logs que se almacenan en SmartVolume/Log/.",
            R.drawable.ic_log,
            false,
            ICON_LAYOUT,
            SWITCH_LAYOUT
    );

    public static Setting refresh_location_setting = new Setting(
            refresh_frequency_setting_id ,
            "Frecuencia de GPS",
            "Puede alterar la precisión de la velocidad.",
            R.drawable.ic_refesh,
            ICON_LAYOUT,
            SPINNER_LAYOUT,
            refreshLocationSegOptions,
            DEFAULT_REFRESH_LOCATION_INDEX
    );





}
