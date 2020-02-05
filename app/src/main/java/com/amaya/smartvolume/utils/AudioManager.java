package com.amaya.smartvolume.utils;

import com.amaya.smartvolume.data.SettingsData;
import com.amaya.smartvolume.services.SharedPreferencesService;

import static com.amaya.smartvolume.fragments.HomeFragment.speed_level_1;
import static com.amaya.smartvolume.fragments.HomeFragment.speed_level_2;
import static com.amaya.smartvolume.fragments.HomeFragment.speed_level_3;
import static com.amaya.smartvolume.fragments.HomeFragment.speed_level_4;
import static com.amaya.smartvolume.fragments.HomeFragment.speed_level_5;

public class AudioManager {

    static Double volume_level_max = 0.95;
    static Double volume_level_0 = 0.2;
    static Double volume_level_1 = 0.3;
    static Double volume_level_2 = 0.5;
    static Double volume_level_3 = 0.7;
    static Double volume_level_4 = 0.85;

    public static int getAudioManagerMode(android.media.AudioManager audioManager) {
        if (audioManager.getMode() == android.media.AudioManager.MODE_IN_CALL) {
            return android.media.AudioManager.STREAM_VOICE_CALL;
        } else {
            return android.media.AudioManager.STREAM_MUSIC;
        }
    }

    public static int getMaxVolume(int maxVolume) {
        Boolean maxVolumeEnable = SharedPreferencesService.getBooleanItem(
                SettingsData.max_volume_setting_id,
                false);
        if (maxVolumeEnable) {
            return maxVolume;
        } else {
            return (int) (maxVolume * volume_level_max);
        }
    }

    public static int getVolumeOfSpeedLevel(int speed, int maxVolume) {
        if (speed == -1) {
            return 0;
        } else if (speed < speed_level_1) {
            return (int) (maxVolume * volume_level_0);
        } else if (speed < speed_level_2) {
            return (int) (maxVolume * volume_level_1);
        } else if (speed < speed_level_3) {
            return (int) (maxVolume * volume_level_2);
        } else if (speed < speed_level_4) {
            return (int) (maxVolume * volume_level_3);
        } else if (speed < speed_level_5) {
            return (int) (maxVolume * volume_level_4);
        } else {
            return getMaxVolume(maxVolume);
        }
    }
}
