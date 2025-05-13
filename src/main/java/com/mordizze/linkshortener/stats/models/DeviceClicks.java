package com.mordizze.linkshortener.stats.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeviceClicks {
    private String device;
    private long count;
}
