package com.lewis.readwriteisolation.core.enums;

public enum RoutingEnum {

    MASTER("master"), SLAVE("slave");

    private String type;

    RoutingEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
