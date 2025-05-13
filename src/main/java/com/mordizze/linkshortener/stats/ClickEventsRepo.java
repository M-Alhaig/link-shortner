package com.mordizze.linkshortener.stats;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mordizze.linkshortener.link.models.ClickEvents;
import com.mordizze.linkshortener.link.models.Link;
import java.time.LocalDateTime;


@Repository
public interface ClickEventsRepo extends JpaRepository<ClickEvents, Long> {

    @Query("SELECT c.country, COUNT(*) f FROM ClickEvents c WHERE c.country IS NOT NULL GROUP BY c.country ORDER BY f DESC")
    List<Object[]> findDistinctCountriesSortedDesc();

    @Query("SELECT c.city, COUNT(*) f FROM ClickEvents c WHERE c.city IS NOT NULL GROUP BY c.city ORDER BY f DESC")
    List<Object[]> findDistinctCitiesSortedDesc();

    @Query("SELECT c.device, COUNT(*) f FROM ClickEvents c WHERE c.device IS NOT NULL GROUP BY c.device ORDER BY f DESC")
    List<Object[]> findDistinctDeviceCount();

    @Query("SELECT c.referrer, COUNT(*) f FROM ClickEvents c WHERE c.referrer IS NOT NULL GROUP BY c.referrer ORDER BY f DESC")
    List<Object[]> findDistnctReferrersCount();

    List<ClickEvents> findByClickedAtAfter(LocalDateTime clickedAt);

    List<ClickEvents> findByLink(Link link);
}
