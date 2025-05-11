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

    @Query("SELECT c.country, COUNT(*) f FROM ClickEvents c WHERE c.country != 'Unknown'  GROUP BY c.country ORDER BY f DESC")
    List<Object[]> findDistinctCountriesSortedDesc();

    @Query("SELECT c.city, COUNT(*) f FROM ClickEvents c WHERE c.city != 'Unknown'  GROUP BY c.city ORDER BY f DESC")
    List<Object[]> findDistinctCitiesSortedDesc();

    @Query("SELECT c.device, COUNT(*) FROM ClickEvents c WHERE c.device != 'Unknown' GROUP BY c.device")
    List<Object[]> findDistinctDeviceCount();

    List<ClickEvents> findByClickedAtAfter(LocalDateTime clickedAt);

    List<ClickEvents> findByLink(Link link);
}
