package com.mordizze.linkshortener.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.model.CountryResponse;

import jakarta.servlet.http.HttpServletRequest;

@Service
@Slf4j
public class GetGeoLocationService {

    private final DatabaseReader databaseReader;

    public GetGeoLocationService(@Value("${max-mind-city-db}") String cityDbPath) throws IOException {
        try (InputStream dbStram = this.getClass().getClassLoader().getResourceAsStream(cityDbPath)) {
            log.info("DB path is: "+cityDbPath);
            if (dbStram == null){
                throw new IOException("GeoIP database not found");
            }
            databaseReader = new DatabaseReader.Builder(dbStram).build();
            log.info("GeoIP database reader initialized");
        } catch (IOException e) {
            log.error("Error initializing GeoIP database reader", e);
            throw e;
        }
    }

    public String getCountry(String ipString) {
        try {
            InetAddress ipAddress = InetAddress.getByName(ipString);
            CountryResponse response = databaseReader.country(ipAddress);
            return response.getCountry().getName();
        } catch (IOException | GeoIp2Exception e) {
            log.error("Error reading Max Mind city Database with ip {}", ipString);
            return "Unknown";
        }
    }

    public String getCity(String ipString) {
        try {
            InetAddress ipAddress = InetAddress.getByName(ipString);
            CityResponse response = databaseReader.city(ipAddress);
            return response.getCity().getName();
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
    

}
