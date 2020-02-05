package com.amaya.smartvolume.models;

import java.util.List;

public class Setting {

    private boolean isVersion = false;

    public boolean isRestore() {
        return isRestore;
    }

    private boolean isRestore= false;

    private String id;
    private String title;
    private String subhead;
    private int icon;
    private boolean defaultCheck;
    private int layout;

    private List<String> spinnerItems;

    public Setting(boolean isRestore, String title) {
        this.isRestore = isRestore;
        this.title = title;
    }

    public Setting(boolean isVersion) {
        this.isVersion = isVersion;
    }

    public Setting(String id, String title, String subhead, int icon, boolean defaultCheck,
                   int layout) {
        this.id = id;
        this.title = title;
        this.subhead = subhead;
        this.icon = icon;
        this.defaultCheck = defaultCheck;
        this.layout = layout;
    }

    public Setting(String id, String title, String subhead, int icon, boolean defaultCheck,
                   int layout, List<String> spinnerItems) {
        this.id = id;
        this.title = title;
        this.subhead = subhead;
        this.icon = icon;
        this.defaultCheck = defaultCheck;
        this.layout = layout;
        this.spinnerItems = spinnerItems;
    }

    public boolean isVersion() {
        return isVersion;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSubhead() {
        return subhead;
    }
    public int getIcon() {
        return icon;
    }

    public int getLayout() {
        return layout;
    }

    public List<String> getSpinnerItems() {
        return spinnerItems;
    }

    public boolean getDefaultCheck() {
        return defaultCheck;
    }
}
