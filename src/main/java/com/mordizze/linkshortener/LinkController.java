package com.mordizze.linkshortener;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mordizze.linkshortener.services.ShortenLinkService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class LinkController {

    private final ShortenLinkService shortenLinkService;

    @PostMapping("/shorten/{url}")
    public ResponseEntity<String> shortenLink(@PathVariable String url) {
        return shortenLinkService.execute(url);
    }

    @GetMapping("/{short_code}")
    public ResponseEntity<String> redirectToOriginalUrl(@PathVariable String short_code) {
        return ResponseEntity.ok("Hello World");
    }
}
