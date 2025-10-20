package com.mordizze.linkshortener.link.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mordizze.linkshortener.Command;
import com.mordizze.linkshortener.link.LinkRepo;
import com.mordizze.linkshortener.link.models.Link;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShortenLinkService implements Command<String, String> {

    @Value("${app.base.url}")
    private String BASE_URL;
    
    private final int SHORT_CODE_LENGTH = 8;
    private final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-_@";
    private final LinkRepo linkRepo;

    @Override
    public String execute(String input) {
        try {
            // Validate and normalize the URL
            String normalizedUrl = normalizeUrl(input);
            log.info("Normalized URL: {}", normalizedUrl);
            String shortCode = generateShortCode(normalizedUrl);

            while (linkRepo.findByShortCode(shortCode).isPresent()) {
                shortCode = generateShortCode(normalizedUrl);
            }
            log.info("Short code: {}", shortCode);

            Link link = new Link();
            link.setShortCode(shortCode);
            link.setOriginalUrl(normalizedUrl);
            link.setClickCount(0);
            link.setReturningUsers(new java.util.HashSet<>());
            linkRepo.save(link);
            log.info("Link saved: {}", link);
            return (BASE_URL + "/" + shortCode);
        } catch (URISyntaxException e) {
            log.error("Invalid URL format: {}", input);
            throw new RuntimeException("Invalid URL format: " + input, e);
        }
    }

    private String normalizeUrl(String url) throws URISyntaxException {
        // Add http:// if no protocol is specified
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            log.info("Adding https:// to the URL: {}", url);
            url = "https://" + url;
        }
        
        // Create and normalize the URI
        URI uri = new URI(url);
        return uri.normalize().toString();
    }

    private String generateShortCode(String originalUrl) {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder shortCode = new StringBuilder();
        int alphabetLength = ALPHABET.length();
        for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
            int randomIndex = secureRandom.nextInt(alphabetLength);
            shortCode.append(ALPHABET.charAt(randomIndex));
        }
        return shortCode.toString();
    }

}
