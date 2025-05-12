package com.mordizze.linkshortener.stats.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CityClicks {
    private String city;
    private long count;
}
