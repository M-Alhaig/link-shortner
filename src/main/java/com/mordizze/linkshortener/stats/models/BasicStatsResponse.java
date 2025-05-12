package com.mordizze.linkshortener.stats.models;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BasicStatsResponse {
    private String shortCode;
    private long totalClicks;
    private Map<String, Map<String, Long>> clickOverTime;
    private List<CountryClicks> topCountries;
    private List<CityClicks> topCities;
    private List<DeviceClicks> devices;
}
