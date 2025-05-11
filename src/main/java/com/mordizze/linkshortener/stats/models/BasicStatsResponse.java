package com.mordizze.linkshortener.stats.models;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BasicStatsResponse {
    private String shortCode;
    private long totalClicks;
    private Map<String, Long> clickOverTime;
    private String[] topCountries;
    private String[] topCities;
    private Map<String, Long> devices;
}
