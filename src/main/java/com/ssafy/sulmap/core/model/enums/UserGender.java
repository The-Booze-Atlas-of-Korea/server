package com.ssafy.sulmap.core.model.enums;

public enum UserGender {
    MALE,
    FEMALE,
    OTHER,
    UNKNOWN;

    public static UserGender fromString(String value)
    {
        return switch (value) {
            case "M" -> MALE;
            case "F" -> FEMALE;
            case "OTHER" -> OTHER;
            case "UNKNOWN" -> UNKNOWN;
            default -> throw new IllegalStateException("Unexpected value: " + value);
        };
    }

    @Override
    public String toString() {
        if(this.equals(MALE)) return "M";
        else if(this.equals(FEMALE)) return "F";
        else if(this.equals(OTHER)) return "OTHER";
        else if(this.equals(UNKNOWN)) return "UNKNOWN";
        return this.name();
    }
}
