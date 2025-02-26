package com.example.demo.user;

/**
 * 定义状态枚举
 */
public enum UserStatus {
    YES,
    NO,
    UNKNOWN;

    public static UserStatus fromValue(String value) {
        for (UserStatus status : values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No matching UserStatus for value: " + value);
    }
}