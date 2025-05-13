package com.mordizze.linkshortener.link;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mordizze.linkshortener.link.models.Link;

@Repository
public interface LinkRepo extends JpaRepository<Link, Long> {

    Optional<Link> findByShortCode(String shortCode);

}
