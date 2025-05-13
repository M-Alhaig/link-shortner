package com.mordizze.linkshortener.link;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mordizze.linkshortener.link.models.RedirectRequest;
import com.mordizze.linkshortener.link.services.RedirectRequestService;
import com.mordizze.linkshortener.link.services.ShortenLinkService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@AllArgsConstructor
@Slf4j
public class LinkController {

    private final ShortenLinkService shortenLinkService;
    private final RedirectRequestService redirectRequestService;

    @PostMapping("/shorten")
    public ResponseEntity<String> shortenLink(@RequestBody String url) {
        String shortenedLink = shortenLinkService.execute(url);
        return ResponseEntity.ok().body(shortenedLink);
    }

    @GetMapping("/{short_code}")
    public ResponseEntity<URI> redirectToOriginalUrl(@PathVariable String short_code,
                                                    HttpServletRequest request) {
        URI uri = redirectRequestService.execute(new RedirectRequest(short_code, request));
        if (uri != null)
            return ResponseEntity.status(HttpStatus.FOUND).location(uri).build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
}
