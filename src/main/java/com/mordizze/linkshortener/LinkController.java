package com.mordizze.linkshortener;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mordizze.linkshortener.services.RedirectRequestService;
import com.mordizze.linkshortener.services.ShortenLinkService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class LinkController {

    private final ShortenLinkService shortenLinkService;
    private final RedirectRequestService redirectRequestService;

    @PostMapping("/shorten")
    public ResponseEntity<String> shortenLink(@RequestBody String url) {
        return shortenLinkService.execute(url);
    }

    @GetMapping("/{short_code}")
    public ResponseEntity<URI> redirectToOriginalUrl(@PathVariable String short_code) {
        return redirectRequestService.execute(short_code);
    }
}
