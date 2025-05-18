package com.mordizze.linkshortener.stats.services;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.mordizze.linkshortener.link.models.ClickEvents;
import com.mordizze.linkshortener.link.models.Link;
import com.mordizze.linkshortener.stats.ClickEventsRepo;
import com.mordizze.linkshortener.stats.models.ClickDistributionResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Service
@Data
@AllArgsConstructor
@Slf4j
public class ClickDistributionService {
    private final ClickEventsRepo clickEventsRepo;
    private final CacheManager cacheManager;
    private final String CACHE = "CLICK_DISTRIBUTION";
    private final int INTERVAL_WEEKS = 4;

    public void caculate(Link link) {
        LocalDateTime after = LocalDateTime.now().minusWeeks(INTERVAL_WEEKS);
        List<ClickEvents> events = clickEventsRepo.findByClickedAtAfterAndLinkOrderByClickedAt(after, link);

        Map<String, Long> hourlyDistribution = new TreeMap<>();
        Map<String, Long> dailyDistribution = new TreeMap<>();

        for (int i=0; i < 24; i++) {
            String formated = String.format("%02d", i);
            hourlyDistribution.put(formated, 0L);
        }

        for (DayOfWeek day : DayOfWeek.values()) {
            dailyDistribution.put(day.name(), 0L);
        }

        events.stream().map(event -> {
            LocalDateTime dateTime = event.getClickedAt();
            String formated = String.format("%02d", dateTime.getHour());
            return formated;
        }).collect(Collectors.groupingBy(
            hour -> hour,
            Collectors.counting()
        ))
        .forEach((hour, count) -> {
            if (hourlyDistribution.containsKey(hour))
                hourlyDistribution.put(hour, count);
        });

        events.stream().map(event -> event.getClickedAt().getDayOfWeek().name())
        .collect(Collectors.groupingBy(
            day -> day,
            Collectors.counting()
        ))
        .forEach((day, count) -> {
            if (dailyDistribution.containsKey(day))
                dailyDistribution.put(day, count);
        });
        

        ClickDistributionResponse response = new ClickDistributionResponse(link.getShortCode(), hourlyDistribution, dailyDistribution);

        Cache cache = cacheManager.getCache(CACHE);
        if (cache == null) {
            log.error("Cache {} Not Found", CACHE);
            throw new IllegalStateException("Error Putting Values in Cache "+CACHE);
        }

        log.info("AverageTimeBetweenClicks Successfully Caculated For Short Code {}", link.getShortCode());
        cache.put(link.getShortCode(), response);
    }
}
