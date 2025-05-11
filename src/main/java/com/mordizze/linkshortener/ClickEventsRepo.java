package com.mordizze.linkshortener;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.mordizze.linkshortener.models.ClickEvents;

@Repository
public interface ClickEventsRepo extends JpaRepository<ClickEvents, Long> {

}
