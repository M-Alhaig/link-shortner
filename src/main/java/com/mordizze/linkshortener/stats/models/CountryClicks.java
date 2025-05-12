package com.mordizze.linkshortener.stats.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CountryClicks {
    private String country;
    private long count;
}
