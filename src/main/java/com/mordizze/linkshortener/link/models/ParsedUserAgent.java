package com.mordizze.linkshortener.link.models;

public record ParsedUserAgent(
    String browser,
    String browserVersion,
    String os,
    String device
) {}
