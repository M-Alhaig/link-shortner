package com.mordizze.linkshortner.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.User;

public interface UserRepo extends JpaRepository<User, String> {

}
