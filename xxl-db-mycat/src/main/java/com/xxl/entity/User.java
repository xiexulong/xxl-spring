package com.xxl.entity;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = -1724273397574256042L;

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
