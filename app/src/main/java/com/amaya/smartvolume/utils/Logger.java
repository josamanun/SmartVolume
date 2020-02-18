package com.amaya.smartvolume.utils;

import android.os.Environment;

import com.amaya.smartvolume.data.SettingsData;
import com.amaya.smartvolume.services.SharedPreferencesService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    public static String LOG_DIRECTORY_PATH =
            Environment.getExternalStorageDirectory() + File.separator + "SmartVolume/Log";

    public static void logOnNote(String sBody) {
        Boolean logEnable = SharedPreferencesService.getBooleanItem(SettingsData.setting_enable_log_id, false);
        if (logEnable) {
            SimpleDateFormat now_formatter = new SimpleDateFormat("yyyy_MM_dd");
            Date now = new Date();
            String fileName = now_formatter.format(now) + ".txt"; //like 2016_01_12.txt

            SimpleDateFormat logFormatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
            Date log = new Date();
            String logMoment = logFormatter.format(log) + ":  "; //like 2016_01_12_13_50_23:

            try {
                File root = new File(LOG_DIRECTORY_PATH);
                if (!root.exists()) {
                    root.mkdirs();
                }
                File gpxfile = new File(root, fileName);
                FileWriter writer = new FileWriter(gpxfile, true);
                writer.append(logMoment + sBody + "\n");
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
