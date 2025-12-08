package com.ssafy.sulmap.core.model.enums;

public enum UserStatus {
    ACTIVE,
    WITHDRAWN,
    BANNED;

    public static UserStatus fromString(String value){
        return switch (value) {
            case "ACTIVE" -> ACTIVE;
            case "WITHDRAWN" -> WITHDRAWN;
            case "BANNED" -> BANNED;
            default -> throw new IllegalStateException("Unexpected value: " + value);
        };
    }

    public String toString(){
        return this.name();
    }
}
