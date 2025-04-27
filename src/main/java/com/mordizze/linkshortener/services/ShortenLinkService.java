package com.mordizze.linkshortener.services;

import java.security.SecureRandom;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.mordizze.linkshortener.Command;
import com.mordizze.linkshortener.Link;
import com.mordizze.linkshortener.LinkRepo;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class ShortenLinkService implements Command<String, String> {

    private final int SHORT_CODE_LENGTH = 8;
    private final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-_@";
    private final LinkRepo linkRepo;

    @Override
    public ResponseEntity<String> execute(String input) {
        String shortCode = generateShortCode(input);

        while (linkRepo.findById(shortCode).isPresent()) {
            shortCode = generateShortCode(input);
        }
        
        Link link = new Link();
        link.setShortCode(shortCode);
        link.setOriginalUrl(input);
        link.setClickCount(0);
        linkRepo.save(link);
        return ResponseEntity.ok(shortCode);
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
