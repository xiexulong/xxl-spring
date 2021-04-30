package com.xxl.entity;


public class CustomConfigAttribute {

    private Boolean isGranted;

    private String attribute;

    public Boolean getGranted() {
        return isGranted;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public CustomConfigAttribute(String role, Boolean isGranted) {
        this.attribute = role;
        this.isGranted = isGranted;
    }

    public Boolean isGranted() {
        return isGranted;
    }

    public void setGranted(Boolean granted) {
        isGranted = granted;
    }

    @Override
    public String toString() {
        return "CustomConfigAttribute{"
                + "Role=" + this.getAttribute()
                + ",isGranted=" + isGranted
                + '}';
    }
}
