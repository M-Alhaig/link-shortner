package com.mordizze.linkshortener.services;

import org.springframework.stereotype.Service;

import com.mordizze.linkshortener.models.ParsedUserAgent;

import lombok.extern.slf4j.Slf4j;
import ua_parser.Client;
import ua_parser.Parser;

@Service
@Slf4j
public class UserAgentService {

    private final Parser parser = new Parser();    

    public ParsedUserAgent parseUserAgent(String userAgent) {
        log.info("Parsing User Agent {}", userAgent);
        if (userAgent == null || userAgent.isEmpty()) {
            log.info("User Agent is null or empty");
            userAgent = "Unknown";
        }

        Client client = parser.parse(userAgent);
        return new ParsedUserAgent(
            safe(client.userAgent.family),
            safe(client.userAgent.major),
            safe(client.os.family),
            safe(client.device.family)
        );
    }
    
    private String safe(String value) {
        return value != null && !value.isBlank() ? value : "Unknown";
    }
}
