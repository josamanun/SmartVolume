package com.amaya.smartvolume.models;

import java.util.List;

public class Setting {

    private String type;
    private String id;
    private String title;
    private String subhead;

    private String text;
    private int icon;
    private boolean defaultCheck;
    private int defualtValueIndex;

    private int left_layout;
    private int right_layout;

    private List<String> spinnerItems;

    public Setting(String type, String title) {
        this.type= type;
        this.title = title;
    }

    public Setting(String id, String title, String subhead, int icon, boolean defaultCheck,
            int left_layout, int right_layout) {
        this.id = id;
        this.title = title;
        this.subhead = subhead;
        this.icon = icon;
        this.defaultCheck = defaultCheck;
        this.left_layout = left_layout;
        this.right_layout = right_layout;
    }

    public Setting(String id, String title, String subhead, int icon,
                   int left_layout, int right_layout, List<String> spinnerItems, int defualtValueIndex) {
        this.id = id;
        this.title = title;
        this.subhead = subhead;
        this.icon = icon;
        this.left_layout = left_layout;
        this.right_layout = right_layout;
        this.spinnerItems = spinnerItems;
        this.defualtValueIndex = defualtValueIndex;
    }

    public Setting(String id, String title, String subhead, String text,
                   int left_layout, int right_layout, List<String> spinnerItems, int defualtValueIndex) {
        this.id = id;
        this.title = title;
        this.subhead = subhead;
        this.text = text;
        this.left_layout = left_layout;
        this.right_layout = right_layout;
        this.spinnerItems = spinnerItems;
        this.defualtValueIndex = defualtValueIndex;
    }

    public String getType() {
        return type;
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

    public int getRightLayout() {
        return right_layout;
    }

    public int getLeftLayout() {
        return left_layout;
    }

    public String getText() {
        return text;
    }

    public List<String> getSpinnerItems() {
        return spinnerItems;
    }

    public boolean getDefaultCheck() {
        return defaultCheck;
    }

    public int getDefualtValueIndex() {
        return defualtValueIndex;
    }
}
