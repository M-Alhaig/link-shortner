package com.mordizze.linkshortener.stats.services;

import org.springframework.stereotype.Service;

import com.mordizze.linkshortener.Command;
import com.mordizze.linkshortener.link.LinkRepo;
import com.mordizze.linkshortener.link.models.Link;
import com.mordizze.linkshortener.stats.ClickEventsRepo;
import com.mordizze.linkshortener.stats.models.AdvancedStatsRequest;
import com.mordizze.linkshortener.stats.models.AdvancedStatsResponse;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class GetAdvancedStats implements Command<AdvancedStatsRequest, AdvancedStatsResponse>{

    private final ClickEventsRepo clickEventsRepo;
    private final LinkRepo linkRepo;

    @Override
    public AdvancedStatsResponse execute(AdvancedStatsRequest input) {
        // TODO Auto-generated method stub
        String shortCode = input.getShortCode();

        Link link = linkRepo.findByShortCode(shortCode).orElseThrow( () -> new RuntimeException("Invalid Short Code"));

        int returningUsersCount = link.getReturningUsers().size();

        return new AdvancedStatsResponse(shortCode,
                                         returningUsersCount);
    }


}
