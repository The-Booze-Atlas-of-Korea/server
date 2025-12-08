package com.ssafy.sulmap.core.model.enums;

public enum UserProfileVisitVisibility {
    PUBLIC,
    FRIENDS,
    PRIVATE;

    public static UserProfileVisitVisibility fromString(String value)
    {
        return switch (value) {
            case "PUBLIC" -> UserProfileVisitVisibility.PUBLIC;
            case "FRIENDS" -> UserProfileVisitVisibility.FRIENDS;
            case "PRIVATE" -> UserProfileVisitVisibility.PRIVATE;
            default -> throw new IllegalStateException("Unexpected value: " + value);
        };
    }

    public String toString()
    {
        return this.name();
    }
}
