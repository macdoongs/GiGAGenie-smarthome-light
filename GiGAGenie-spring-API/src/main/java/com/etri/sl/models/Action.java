package com.etri.sl.models;

import java.io.Serializable;

public class Action implements Serializable{
    private String name;
    private String unit;
    private float unitId;
    private String attribute;
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
