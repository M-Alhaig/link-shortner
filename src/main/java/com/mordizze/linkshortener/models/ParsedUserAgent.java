package com.mordizze.linkshortener.models;

public record ParsedUserAgent(
    String browser,
    String browserVersion,
    String os,
    String device
) {}
