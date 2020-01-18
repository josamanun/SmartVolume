package com.amaya.smartvolume.models;

public class Setting {

    private String id;
    private String title;
    private String subhead;
    private int icon;
    private boolean defaultCheck;

    public Setting(String id, String title, String subhead, int icon, boolean defaultCheck) {
        this.id = id;
        this.title = title;
        this.subhead = subhead;
        this.icon = icon;
        this.defaultCheck = defaultCheck;
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

    public boolean getDefaultCheck() {
        return defaultCheck;
    }
}
