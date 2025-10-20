package com.mordizze.linkshortener.stats.models;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AverageTimeBetweenClicks {
    private double averageTimeBetweenClicks;
    private String unit;
    private String lastUpdated;
}
