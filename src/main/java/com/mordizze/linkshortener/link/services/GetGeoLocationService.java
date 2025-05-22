package com.mordizze.linkshortener.link.services;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.model.CountryResponse;

import jakarta.servlet.http.HttpServletRequest;

@Service
@Slf4j
public class GetGeoLocationService {

    private DatabaseReader databaseReader;
    private final String cityDbPath;
    private boolean initialized = false;
    private final Object initLock = new Object();

    public GetGeoLocationService(@Value("${max-mind-city-db}") String cityDbPath) {
        this.cityDbPath = cityDbPath;
        log.info("GetGeoLocationService created with DB path: {}", cityDbPath);
        // Don't initialize here - do it lazily on first use
    }

    private void initializeIfNeeded() throws IOException {
        if (!initialized) {
            synchronized (initLock) {
                if (!initialized) {
                    log.info("Lazy loading GeoIP database from: {}", cityDbPath);

                    InputStream dbStream = this.getClass().getClassLoader().getResourceAsStream(cityDbPath);
                    if (dbStream == null) {
                        // Try alternative loading methods
                        dbStream = this.getClass().getResourceAsStream("/" + cityDbPath);
                    }

                    if (dbStream == null) {
                        ClassPathResource resource = new ClassPathResource(cityDbPath);
                        if (resource.exists()) {
                            dbStream = resource.getInputStream();
                        }
                    }

                    if (dbStream == null) {
                        throw new IOException("GeoIP database not found at path: " + cityDbPath);
                    }

                    try (BufferedInputStream bufferedStream = new BufferedInputStream(dbStream, 64 * 1024)) {
                        databaseReader = new DatabaseReader.Builder(bufferedStream).build();
                        initialized = true;
                        log.info("GeoIP database reader initialized successfully");
                    }
                }
            }
        }
    }

    public String getCountry(String ipString) {
        try {
            initializeIfNeeded();
            InetAddress ipAddress = InetAddress.getByName(ipString);
            CountryResponse response = databaseReader.country(ipAddress);
            String country = response.getCountry().getName();
            if (country == null)
                country = "Unknown";
            return country;
        } catch (IOException | GeoIp2Exception e) {
            log.error("Error reading Max Mind city Database with ip {}", ipString);
            return "Unknown";
        }
    }

    public String getCity(String ipString) {
        try {
            initializeIfNeeded();
            InetAddress ipAddress = InetAddress.getByName(ipString);
            CityResponse response = databaseReader.city(ipAddress);
            String city = response.getCity().getName();
            if (city == null)
                city = "Unknown";
            return city;
        } catch (Exception e) {
            log.error("Error reading Max Mind city Database with ip {}", ipString);
            return "Unknown";
        }
    }

    public String getClientIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getHeader("X-Real-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr(); 
        }
        if (ipAddress == null || ipAddress.isEmpty()) {
            return "Unknown";
        }
        return ipAddress;
    }

    @PreDestroy
    public void cleanup() {
        if (databaseReader != null) {
            try {
                databaseReader.close();
                log.info("GeoIP database reader closed");
            } catch (IOException e) {
                log.warn("Error closing GeoIP database reader", e);
            }
        }
    }
    

}
