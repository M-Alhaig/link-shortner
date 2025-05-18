package com.mordizze.linkshortener.stats.services;


import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.stereotype.Service;

import com.mordizze.linkshortener.Command;
import com.mordizze.linkshortener.link.LinkRepo;
import com.mordizze.linkshortener.link.models.Link;
import com.mordizze.linkshortener.stats.ClickEventsRepo;
import com.mordizze.linkshortener.stats.models.AdvancedStatsRequest;
import com.mordizze.linkshortener.stats.models.AdvancedStatsResponse;
import com.mordizze.linkshortener.stats.models.AverageTimeBetweenClicks;
import com.mordizze.linkshortener.stats.models.ClickDistributionResponse;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class GetAdvancedStats implements Command<AdvancedStatsRequest, AdvancedStatsResponse>{

    private final ClickEventsRepo clickEventsRepo;
    private final AverageTimeBetweenClicksService averageTimeBetweenClicksService;
    private final ClickDistributionService clickDistributionService;
    private final LinkRepo linkRepo;
    private final CacheManager cacheManager;
    private final String AVERAGE_TIME_BETWEEN_CLICKS_CACHE = "TIME_BETWEEN_CLICKS";
    private final String CLICK_DISTRIBUTION_CACHE = "CLICK_DISTRIBUTION";

    @Override
    public AdvancedStatsResponse execute(AdvancedStatsRequest input) {
        // TODO Auto-generated method stub
        String shortCode = input.getShortCode();

        Link link = linkRepo.findByShortCode(shortCode).orElseThrow( () -> new RuntimeException("Invalid Short Code"));

        int returningUsersCount = link.getReturningUsers().size();

        AverageTimeBetweenClicks averageTimeBetweenClicks = getAverageTimeBetweenClicks(link);
        ClickDistributionResponse clickDistributionResponse = getClickDistribution(link);    

        return new AdvancedStatsResponse(shortCode,
                                         returningUsersCount,
                                         averageTimeBetweenClicks,
                                         clickDistributionResponse);
    }

    private AverageTimeBetweenClicks getAverageTimeBetweenClicks(Link link) {

        Cache cache = cacheManager.getCache(AVERAGE_TIME_BETWEEN_CLICKS_CACHE);
        if (cache == null)
            throw new RuntimeException("Error Fetching Data From Cache "+AVERAGE_TIME_BETWEEN_CLICKS_CACHE);
        ValueWrapper wrapper = cache.get(link.getShortCode());

        if (wrapper == null) {
            log.error("AverageTimeBetweenClicks Not Existent in Cache now calculating");
            averageTimeBetweenClicksService.caculate(link);
            wrapper = cache.get(link.getShortCode());
        }

        return (AverageTimeBetweenClicks) wrapper.get();
    }


    private ClickDistributionResponse getClickDistribution(Link link) {
        Cache cache = cacheManager.getCache(CLICK_DISTRIBUTION_CACHE);
        if (cache == null)
            throw new RuntimeException("Error Fetching Data From Cache "+CLICK_DISTRIBUTION_CACHE);
        ValueWrapper wrapper = cache.get(link.getShortCode());

        if (wrapper == null) {
            log.error("Click Distribution Not Existent in Cache now calculating");
            clickDistributionService.caculate(link);
            wrapper = cache.get(link.getShortCode());
        }

        return (ClickDistributionResponse) wrapper.get();
    }
}
