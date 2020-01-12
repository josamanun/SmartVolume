package com.amaya.smartvolume.services;

import android.content.Context;
import android.content.SharedPreferences;

import com.amaya.smartvolume.R;

import java.util.HashMap;
import java.util.Map;

public class SharedPreferencesService {

    public static String DEFAULT_ITEM_VALUE = "none";

    public static SharedPreferences sharedPreferences;

    public static void initialize(Context context) {
        sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.app_package_path), Context.MODE_PRIVATE);
    }

    public static Map<String,?> getAllItems() {
        return sharedPreferences.getAll();
    }

    public static String getItem(String key) {
        return sharedPreferences.getString(key, DEFAULT_ITEM_VALUE);
    }

    public static Boolean addItems(HashMap<String, String> items) {
        // Instanciamos el editor
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Añadimos los items al editor
        for(String key : items.keySet()) {
            editor.putString(key, items.get(key));
        }
        // Guardamos los cambios
        boolean result = editor.commit();
        editor.apply();

        return result;
    }

    public static void addItem(String key, String value) {
        // Instanciamos el editor
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Añadimos la clave-valor
        editor.putString(key, value);
        // Guardamos los cambios
        editor.commit();
        editor.apply();
    }

    public static void clear() {
        sharedPreferences.edit().clear().commit();
    }

}
