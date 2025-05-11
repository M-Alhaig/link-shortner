package com.mordizze.linkshortener.stats.models;

public enum Interval {
    DAILY, WEEKLY;

    public static Interval fromString(String value) {
        try {
            return Interval.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException();
        }
    }
}
