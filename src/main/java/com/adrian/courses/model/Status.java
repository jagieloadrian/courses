package com.adrian.courses.model;

public enum Status {
    I("INACTIVE"),A("ACTIVE"),F("FULL");

    private String name;

    Status(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
