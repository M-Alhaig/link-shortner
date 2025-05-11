package com.mordizze.linkshortener.models;

import java.sql.Date;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "click_events")
public class ClickEvents {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "link_id", nullable = false)
    private Link link;

    private String referrer;

    private String ipAddress;

    private String browser;

    private String browserVersion;

    private String os;

    private String device;

    private String country;

    private String city;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime clickedAt;

    public ClickEvents(Link link, String referrer, String ipAddress,
                       ParsedUserAgent parsedUserAgent, String country, String city) {
        this.link = link;
        this.referrer = referrer;
        this.ipAddress = ipAddress;
        this.country = country;
        this.city = city;
        this.browser = parsedUserAgent.browser();
        this.browserVersion = parsedUserAgent.browserVersion();
        this.os = parsedUserAgent.os();
        this.device = parsedUserAgent.device();
    }

}
