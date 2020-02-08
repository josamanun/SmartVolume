package com.amaya.smartvolume.data;

import com.amaya.smartvolume.services.SharedPreferencesService;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.amaya.smartvolume.data.SettingsData.refresh_frequency_setting_id;
import static com.amaya.smartvolume.data.SettingsData.speed_level_1_id;
import static com.amaya.smartvolume.data.SettingsData.speed_level_2_id;
import static com.amaya.smartvolume.data.SettingsData.speed_level_3_id;
import static com.amaya.smartvolume.data.SettingsData.speed_level_4_id;
import static com.amaya.smartvolume.data.SettingsData.speed_level_5_id;

public class DataGenerator {

    public static DecimalFormat format0 = new DecimalFormat("#");
    public static DecimalFormat format1 = new DecimalFormat("#.#");

    private static String unit_kmh = " kmh";
    private static String unit_seg = " seg";

    public static final List<String> speedOptions = getSpeedsOptions();
    public static final List<String> refreshLocationSegOptions = getFrequenciesOptions();

    public static int default_speed_level_1 = 20;
    public static int default_speed_level_2 = 40;
    public static int default_speed_level_3 = 60;
    public static int default_speed_level_4 = 80;
    public static int default_speed_level_5 = 100;
    public static String default_frequency = "0,5";

    public static final int getIndexOfDefaultSpeedLevel1 = getSpeedOptionIndex(default_speed_level_1);
    public static final int getIndexOfDefaultSpeedLevel2 = getSpeedOptionIndex(default_speed_level_2);
    public static final int getIndexOfDefaultSpeedLevel3 = getSpeedOptionIndex(default_speed_level_3);
    public static final int getIndexOfDefaultSpeedLevel4 = getSpeedOptionIndex(default_speed_level_4);
    public static final int getIndexOfDefaultSpeedLevel5 = getSpeedOptionIndex(default_speed_level_5);
    public static int getIndexOfDefaultFrequency = getFrequencyOptionIndex(default_frequency);


    public static int getSpeedLevel1() {
        return getSpeedValueOfIndex(SharedPreferencesService.getIntegerItem(speed_level_1_id, getIndexOfDefaultSpeedLevel1));
    }
    public static int getSpeedLevel2() {
        return getSpeedValueOfIndex(SharedPreferencesService.getIntegerItem(speed_level_2_id, getIndexOfDefaultSpeedLevel2));
    }
    public static int getSpeedLevel3() {
        return getSpeedValueOfIndex(SharedPreferencesService.getIntegerItem(speed_level_3_id, getIndexOfDefaultSpeedLevel3));
    }

    public static int getSpeedLevel4() {
        return getSpeedValueOfIndex(SharedPreferencesService.getIntegerItem(speed_level_4_id, getIndexOfDefaultSpeedLevel4));
    }

    public static int getSpeedLevel5() {
        return getSpeedValueOfIndex(SharedPreferencesService.getIntegerItem(speed_level_5_id, getIndexOfDefaultSpeedLevel5));
    }

    public static double getFrequency() {
        return getFrequencyValueOfIndex(SharedPreferencesService.getIntegerItem(refresh_frequency_setting_id, getIndexOfDefaultFrequency));
    }

    public static ArrayList<String> getSpeedsOptions() {
        ArrayList<String> result = new ArrayList<>();
        for (int i = 15; i <= 120; i = i+5) {
            result.add(i + unit_kmh);
        }
        return result;
    }

    public static ArrayList<String> getFrequenciesOptions() {
        ArrayList<String> result = new ArrayList<>();
        for (double i = 0.1; i <= 1.0; i = i + 0.1) {
            result.add(format1.format(i) + unit_seg);
        }
        return result;
    }

    public static int getSpeedOptionIndex(int speed) {
        return getSpeedsOptions().indexOf(speed + unit_kmh);
    }

    private static int getFrequencyOptionIndex(String frequency) {
        return getFrequenciesOptions().indexOf(frequency + unit_seg);
    }

    private static int getSpeedValueOfIndex(int index) {
        String speed = speedOptions.get(index);
        return Integer.parseInt(speed.split(" ")[0]);
    }

    private static double getFrequencyValueOfIndex(int index) {
        String frequency = refreshLocationSegOptions.get(index);
        return Double.parseDouble(frequency.split(" ")[0].replace(",","."));
    }
}
