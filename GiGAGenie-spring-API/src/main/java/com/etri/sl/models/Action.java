package com.etri.sl.models;

public class Action {
    private String name;
    private String uspace;
    private String unit;
    private float unitId;
    private String attribute;
    private float value;
    private String color;
    private String command;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUspace() {
        return uspace;
    }

    public void setUspace(String uspace) {
        this.uspace = uspace;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public float getUnitId() {
        return unitId;
    }

    public void setUnitId(float unitId) {
        this.unitId = unitId;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

}
