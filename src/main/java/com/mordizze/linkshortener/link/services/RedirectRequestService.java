package com.mordizze.linkshortener.link.services;

import java.net.URI;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mordizze.linkshortener.Command;
import com.mordizze.linkshortener.link.LinkRepo;
import com.mordizze.linkshortener.link.models.ClickEvents;
import com.mordizze.linkshortener.link.models.Link;
import com.mordizze.linkshortener.link.models.ParsedUserAgent;
import com.mordizze.linkshortener.link.models.RedirectRequest;
import com.mordizze.linkshortener.stats.ClickEventsRepo;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@AllArgsConstructor
@Slf4j
public class RedirectRequestService implements Command<RedirectRequest, URI> {

    private final LinkRepo linkRepo;
    private final ClickEventsRepo clickEventsRepo;
    private final GetGeoLocationService getGeoLocationService;
    private final UserAgentService userAgentService;

    @Override
    public URI execute(RedirectRequest redirectRequest) {
        Optional<Link> link = linkRepo.findByShortCode(redirectRequest.getShortCode());
        if (link.isPresent()) {
            log.info("Redirecting to: {}", link.get().getOriginalUrl());
            HttpServletRequest request = redirectRequest.getRequest();

            // collect info from headers for storage
            String ipString = getGeoLocationService.getClientIp(request);
            String referrer = request.getHeader("Referer");
            String userAgent = request.getHeader("User-Agent");

            log.info("Click came from: {}", referrer);

            // collect location and user agent info for storage
            String city = getGeoLocationService.getCity(ipString);
            String country = getGeoLocationService.getCountry(ipString);
            ParsedUserAgent parsedUserAgent = userAgentService.parseUserAgent(userAgent);

            log.info("User agent info: {}", parsedUserAgent.toString());
            log.info("Country where the click came from: {}", country); 
            log.info("City where the click came from: {}", city);

            link.get().setClickCount(link.get().getClickCount() + 1);
            ClickEvents clicked = new ClickEvents(link.get(), referrer, ipString, parsedUserAgent, country, city);

            log.info(clicked.toString());

            clickEventsRepo.save(clicked);
            linkRepo.save(link.get());
            URI uri = URI.create(link.get().getOriginalUrl());

            return uri;
        }
        return null;
    }

}
