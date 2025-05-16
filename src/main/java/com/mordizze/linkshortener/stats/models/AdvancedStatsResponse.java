package com.mordizze.linkshortener.stats.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdvancedStatsResponse {
    private String shortCode;
    private int returningUsersCount;
}
