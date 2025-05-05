package com.mordizze.linkshortener.services;

import java.net.URI;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.mordizze.linkshortener.Command;
import com.mordizze.linkshortener.Link;
import com.mordizze.linkshortener.LinkRepo;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Service
@AllArgsConstructor
@Slf4j
public class RedirectRequestService implements Command<String, URI> {

    private final LinkRepo linkRepo;

    @Override
    public ResponseEntity<URI> execute(String shortCode) {
        Optional<Link> link = linkRepo.findById(shortCode);
        if (link.isPresent()) {
            link.get().setClickCount(link.get().getClickCount() + 1);
            linkRepo.save(link.get());
            URI uri = URI.create(link.get().getOriginalUrl());
            return ResponseEntity.status(HttpStatus.FOUND).location(uri).build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

}
