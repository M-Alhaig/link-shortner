package com.mordizze.linkshortener.link.services;

import java.net.URI;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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
        Optional<Link> linkOptional = linkRepo.findByShortCode(redirectRequest.getShortCode());
        if (linkOptional.isPresent()) {
            Link link = linkOptional.get();
            log.info("Redirecting to: {}", link.getOriginalUrl());
            HttpServletRequest request = redirectRequest.getRequest();

            // collect info from headers for storage
            String ipString = getGeoLocationService.getClientIp(request);
            String referrer = request.getHeader("Referer");
            String userAgent = request.getHeader("User-Agent");

            if (referrer == null)
                referrer = "Unknown";

            // collect location and user agent info for storage
            String city = getGeoLocationService.getCity(ipString);
            String country = getGeoLocationService.getCountry(ipString);
            ParsedUserAgent parsedUserAgent = userAgentService.parseUserAgent(userAgent);

            link.setClickCount(link.getClickCount() + 1);

            ClickEvents clicked = new ClickEvents(link, referrer, ipString, parsedUserAgent, country, city);

            log.info(clicked.toString());

            if (clickEventsRepo.existsByLinkAndIpAddress(link, ipString)) {
                Set<String> returningUsers = link.getReturningUsers();
                if (returningUsers == null)
                    returningUsers = new HashSet<>();
                returningUsers.add(ipString);
                link.setReturningUsers(returningUsers);
            }

            clickEventsRepo.save(clicked);
            linkRepo.save(link);
            URI uri = URI.create(link.getOriginalUrl());

            return uri;
        }
        return null;
    }

}
