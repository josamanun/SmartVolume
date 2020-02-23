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
    private static long muteTimer = 0;
    public static void resetMuteTimer() {
        muteTimer = 0;
    }
    public static int getVolumeOfSpeedLevel(int speed, int maxVolume) {

        if (speed == -1) return 0;

        if (SharedPreferencesService.getBooleanItem(SettingsData.setting_enable_mute_id, false)) {
            if (speed == 0) {
                if (muteTimer == 0) {
                    muteTimer = System.currentTimeMillis() + (SettingsData.DEFAULT_SECONDS_TO_MUTE * 1000) ;
                } else if (System.currentTimeMillis() > muteTimer) {
                    return 0;
                }
            } else {
                muteTimer = 0;
            }
        }

        if (speed < speed_level_1) return (int) (maxVolume * volume_level_0);
        if (speed < speed_level_2) return (int) (maxVolume * volume_level_1);
        if (speed < speed_level_3) return (int) (maxVolume * volume_level_2);
        if (speed < speed_level_4) return (int) (maxVolume * volume_level_3);
        if (speed < speed_level_5) return (int) (maxVolume * volume_level_4);
        if (speed > speed_level_5) return getMaxVolume(maxVolume);

        return 0;
    }

}
