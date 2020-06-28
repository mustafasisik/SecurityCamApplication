package com.securitycam.models;

public class System {
    private String id, name, code;
    private boolean isSelected, isHost;

    public System(String id, String name, String code, boolean isSelected, boolean isHost) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.isSelected = isSelected;
        this.isHost = isHost;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isHost() {
        return isHost;
    }

    public void setHost(boolean host) {
        isHost = host;
    }
}
