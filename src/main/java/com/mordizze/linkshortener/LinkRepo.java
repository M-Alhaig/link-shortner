package com.mordizze.linkshortener;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mordizze.linkshortener.models.Link;

@Repository
public interface LinkRepo extends JpaRepository<Link, Long> {

    Optional<Link> findByShortCode(String shortCode);

}
