package com.mordizze.linkshortener.stats;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mordizze.linkshortener.stats.models.BasicStatsRequest;
import com.mordizze.linkshortener.stats.models.BasicStatsResponse;
import com.mordizze.linkshortener.stats.services.GetBasicStats;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/{shortCode}/stats")
@AllArgsConstructor
public class StatsController {

    private final GetBasicStats getBasicStats;

    @GetMapping
    public ResponseEntity<BasicStatsResponse> getBasicStats(@PathVariable String shortCode, 
                                @RequestParam(defaultValue = "daily") String interval) {
        BasicStatsResponse response = getBasicStats.execute(new BasicStatsRequest(shortCode, interval));
        return ResponseEntity.ok(response);
    }
    

}
