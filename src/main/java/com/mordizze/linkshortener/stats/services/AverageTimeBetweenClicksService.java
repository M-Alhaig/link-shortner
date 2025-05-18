package com.mordizze.linkshortener.stats.services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import com.mordizze.linkshortener.link.models.ClickEvents;
import com.mordizze.linkshortener.link.models.Link;
import com.mordizze.linkshortener.stats.ClickEventsRepo;
import com.mordizze.linkshortener.stats.models.AverageTimeBetweenClicks;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class AverageTimeBetweenClicksService {

    private final int INTERVAL_DAYS = 7;
    private final String CACHE = "TIME_BETWEEN_CLICKS";
    private final ClickEventsRepo clickEventsRepo;
    private final CacheManager cacheManager;

    public void caculate(Link link) {
        String unit = "seconds";
        LocalDateTime date = LocalDateTime.now().minusDays(INTERVAL_DAYS);
        List<LocalDateTime> clickEvents = clickEventsRepo.findByClickedAtAfterAndLinkOrderByClickedAt(date, link)
                                                         .stream()
                                                         .map(ClickEvents::getClickedAt).toList();


        long sum = 0;
        for (int i = 1; i < clickEvents.size(); i++){
            LocalDateTime current = clickEvents.get(i);
            LocalDateTime previous = clickEvents.get(i - 1);
            Duration difference = Duration.between(previous, current);
            sum += difference.getSeconds();
        }

        double avg = sum / clickEvents.size();

        if (avg > 60) {
            avg /= 60;
            unit = "minutes";
        }
        if (avg > 60) {
            avg /= 60;
            unit = "hours";
        }

        AverageTimeBetweenClicks averageTimeBetweenClicks = new AverageTimeBetweenClicks(avg, unit, LocalDateTime.now().toString());
        Cache cache = cacheManager.getCache(CACHE);
        if (cache != null) {
            cache.put(link.getShortCode(), averageTimeBetweenClicks);
            log.info("AverageTimeBetweenClicks Successfully Caculated For Short Code {}", link.getShortCode());
        } else {
            log.error("Cache {} Not Found", CACHE);
            throw new IllegalStateException("Error Putting Values in Cache "+CACHE);
        }
    }

}
