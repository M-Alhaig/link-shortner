package com.mordizze.linkshortener;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.mordizze.linkshortener.models.RedirectRequest;
import com.mordizze.linkshortener.services.RedirectRequestService;
import com.mordizze.linkshortener.services.ShortenLinkService;

import jakarta.servlet.http.HttpServlet;
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
        return shortenLinkService.execute(url);
    }

    @GetMapping("/{short_code}")
    public ResponseEntity<URI> redirectToOriginalUrl(@PathVariable String short_code,
                                                    HttpServletRequest request) {
        return redirectRequestService.execute(new RedirectRequest(short_code, request));
    }
}
