package com.securitycam.models;

public class Data {
    private String id, image, name, date;
    private int safetyPercent, minSecurityPercent;
    private boolean isRegular;

    public Data(String id, String image, String name, String date, int safetyPercent, int minSecurityPercent, boolean isRegular) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.date = date;
        this.safetyPercent = safetyPercent;
        this.minSecurityPercent = minSecurityPercent;
        this.isRegular = isRegular;
    }

    public boolean isRegular() {
        return isRegular;
    }

    public void setRegular(boolean regular) {
        isRegular = regular;
    }

    public int getMinSecurityPercent() {
        return minSecurityPercent;
    }

    public void setMinSecurityPercent(int minSecurityPercent) {
        this.minSecurityPercent = minSecurityPercent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getSafetyPercent() {
        return safetyPercent;
    }

    public void setSafetyPercent(int safetyPercent) {
        this.safetyPercent = safetyPercent;
    }
}

