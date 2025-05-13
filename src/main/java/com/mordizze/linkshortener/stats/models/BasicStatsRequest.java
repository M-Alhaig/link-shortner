package com.mordizze.linkshortener.stats.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BasicStatsRequest {
    private String shortCode;
    private String interval;
}
