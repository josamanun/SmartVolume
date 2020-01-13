package com.amaya.smartvolume.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.amaya.smartvolume.activities.MainActivity.TAG;

public class Logger {

    public static void logOnNote(String sBody) {

        SimpleDateFormat now_formatter = new SimpleDateFormat("yyyy_MM_dd");
        Date now = new Date();
        String fileName = now_formatter.format(now) + ".txt"; //like 2016_01_12.txt

        SimpleDateFormat logFormatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        Date log = new Date();
        String logMoment = logFormatter.format(log) + ":  "; //like 2016_01_12_13_50_23:

        try {
            File root = new File(Environment.getExternalStorageDirectory()
                    + File.separator+"SmartVolume", "Log");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, fileName);
            FileWriter writer = new FileWriter(gpxfile, true);
            writer.append(logMoment + sBody + "\n");
            writer.flush();
            writer.close();
            Log.i(TAG, "logOnNote: Saved!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
