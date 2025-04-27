package com.mordizze.linkshortener.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<CustomUser, String> {

}
