package com.mordizze.linkshortener.link.services;

import java.io.*;
import java.net.InetAddress;
import java.net.URL;

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

    public GetGeoLocationService(@Value("${max-mind-city-db}") String downloadUrl) throws IOException {
        log.info("Initializing GeoIP database from S3 URL: {}", downloadUrl);

        try {
            // Download the file from S3
            File dbFile = downloadGeoLiteDatabase(downloadUrl);

            // Initialize the DatabaseReader with the downloaded file
            databaseReader = new DatabaseReader.Builder(dbFile).build();

            // Clean up the temp file after DatabaseReader is created
            dbFile.deleteOnExit();

            log.info("GeoIP database reader initialized successfully from S3");

        } catch (IOException e) {
            log.error("Error initializing GeoIP database reader from S3: {}", downloadUrl, e);
            throw new IOException("Failed to initialize GeoIP database from S3: " + e.getMessage(), e);
        }
    }

    private File downloadGeoLiteDatabase(String downloadUrl) throws IOException {
        log.info("Downloading GeoLite2 database from S3...");

        URL url = new URL(downloadUrl);
        File tempFile = File.createTempFile("geolite2-city", ".mmdb");

        try (InputStream in = url.openStream();
             BufferedInputStream bufferedIn = new BufferedInputStream(in, 64 * 1024);
             FileOutputStream out = new FileOutputStream(tempFile);
             BufferedOutputStream bufferedOut = new BufferedOutputStream(out, 64 * 1024)) {

            byte[] buffer = new byte[64 * 1024]; // 64KB buffer for better performance
            int bytesRead;
            long totalBytes = 0;
            long startTime = System.currentTimeMillis();

            while ((bytesRead = bufferedIn.read(buffer)) != -1) {
                bufferedOut.write(buffer, 0, bytesRead);
                totalBytes += bytesRead;

                // Log progress every 10MB
                if (totalBytes % (10 * 1024 * 1024) == 0) {
                    long elapsed = System.currentTimeMillis() - startTime;
                    double speed = (totalBytes / 1024.0 / 1024.0) / (elapsed / 1000.0);
                    log.info("Downloaded {} MB ({} MB/s)", totalBytes / (1024 * 1024), speed);
                }
            }

            long elapsed = System.currentTimeMillis() - startTime;
            double totalMB = totalBytes / (1024.0 * 1024.0);
            double avgSpeed = totalMB / (elapsed / 1000.0);

            log.info("Successfully downloaded GeoLite2 database: {} MB in {}s (avg {} MB/s)",
                    totalMB, elapsed / 1000.0, avgSpeed);

        } catch (IOException e) {
            // Clean up temp file if download failed
            if (tempFile.exists()) {
                tempFile.delete();
            }
            throw new IOException("Failed to download GeoLite2 database from S3: " + e.getMessage(), e);
        }

        return tempFile;
    }

    public String getCountry(String ipString) {
        try {
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
    

}
