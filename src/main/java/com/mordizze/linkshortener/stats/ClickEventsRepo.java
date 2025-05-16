package com.mordizze.linkshortener.stats;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mordizze.linkshortener.link.models.ClickEvents;
import com.mordizze.linkshortener.link.models.Link;
import com.mordizze.linkshortener.stats.models.CityClicks;
import com.mordizze.linkshortener.stats.models.CountryClicks;
import com.mordizze.linkshortener.stats.models.DeviceClicks;
import com.mordizze.linkshortener.stats.models.ReferrerClicks;

import java.time.LocalDateTime;


@Repository
public interface ClickEventsRepo extends JpaRepository<ClickEvents, Long> {

    @Query("""
        SELECT new com.mordizze.linkshortener.stats.models.CountryClicks(c.country, COUNT(*))
         FROM ClickEvents c WHERE c.country IS NOT NULL AND c.link = :link GROUP BY c.country ORDER BY COUNT(*) DESC
        """)
    List<CountryClicks> findTopCountries(Pageable pageable, Link link);

    @Query("""
        SELECT new com.mordizze.linkshortener.stats.models.CityClicks(c.city, COUNT(*))
         FROM ClickEvents c WHERE c.city IS NOT NULL AND c.link = :link GROUP BY c.city ORDER BY COUNT(*) DESC
         """)
    List<CityClicks> findTopCities(Pageable pageable, Link link);

    @Query("""
        SELECT new com.mordizze.linkshortener.stats.models.DeviceClicks(c.device, COUNT(*))
         FROM ClickEvents c WHERE c.device IS NOT NULL AND c.link = :link GROUP BY c.device ORDER BY COUNT(*) DESC
         """)
    List<DeviceClicks> findTopDevices(Pageable pageable, Link link);

    @Query("""
        SELECT new com.mordizze.linkshortener.stats.models.ReferrerClicks(c.referrer, COUNT(*))
         FROM ClickEvents c WHERE c.referrer IS NOT NULL AND c.link = :link GROUP BY c.referrer ORDER BY COUNT(*) DESC
         """)
    List<ReferrerClicks> findTopReferrers(Pageable pageable, Link link);

    List<ClickEvents> findByClickedAtAfterAndLink(LocalDateTime clickedAt, Link link);

    List<ClickEvents> findByLink(Link link);

    boolean existsByLinkAndIpAddress(Link link, String ipAddress);
}
