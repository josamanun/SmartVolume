package com.amaya.smartvolume.data;

import com.amaya.smartvolume.R;
import com.amaya.smartvolume.models.Setting;

public class SettingsData {

    public static Setting[] getSettings() {

        Setting[] settings = {
                new Setting(
                        "setting_max_volume",
                        "Habilitar volumen máximo",
                        "Esto puede dañar los altavoces al alcanzar el volumen al 100%",
                        R.drawable.ic_volume,
                        false
                )
        };
        return settings;
    }

}
