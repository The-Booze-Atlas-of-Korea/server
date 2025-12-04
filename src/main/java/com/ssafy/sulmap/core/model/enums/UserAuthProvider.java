package com.ssafy.sulmap.core.model.enums;

public enum UserAuthProvider {
    LOCAL,
    GOOGLE;

    public static UserAuthProvider fromValue(String value)
    {
        return switch (value) {
            case "LOCAL" -> LOCAL;
            case "GOOGLE" -> GOOGLE;
            default -> throw new IllegalStateException("Unexpected value: " + value);
        };
    }

    public String toString()
    {
        return this.name();
    }
}
