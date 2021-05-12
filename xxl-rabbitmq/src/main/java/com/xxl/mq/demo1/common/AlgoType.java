package com.xxl.mq.demo1.common;

import java.util.HashMap;
import java.util.Map;

public enum AlgoType {

    A1("one","one" ),
    A2("two", "two");




    private final String fullName;
    private final String command;

    private static final Map<String, AlgoType> fullNameToEnum = new HashMap<>();

    static {
        for (AlgoType algoType : AlgoType.values()) {
            fullNameToEnum.put(algoType.fullName.toUpperCase(), algoType);
        }
    }

    AlgoType(String fullName, String command) {
        this.fullName = fullName;
        this.command = command;
    }



    public String getName() {
        return this.name().toLowerCase();
    }

    public String getFullName() {
        return fullName;
    }

    public String getCommand() {
        return command;
    }

}
