package com.mordizze.linkshortener.stats.models;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClickDistributionResponse {
    private String shortCode;
    private Map<String, Long> hourly;
    private Map<String, Long> daily;
}
