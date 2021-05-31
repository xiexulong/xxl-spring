package com.xxl.enums;

import java.util.HashMap;
import java.util.Map;

public enum ConfigType {

    NO_REFRESH("no refresh", "0"), REFRESH("refresh", "1"), READ_ONLY("read only", "2");

    private String display;
    private String value;
    private static Map<String, ConfigType> relationMap = new HashMap<>();

    static {
        for (ConfigType configType : ConfigType.values()) {
            relationMap.put(configType.getValue(), configType);
        }
    }

    ConfigType(String name, String value) {
        this.display = name;
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public String getDisplay() {
        return display;
    }

    public int getIntValue() {
        return Integer.parseInt(this.value);
    }

    public static ConfigType findDisplay(String value) {
        return relationMap.get(value);
    }
}
