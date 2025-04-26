package com.mordizze.linkshortner.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;




public class UserDetailService implements UserDetailsService {

    private final UserRepo userRepo;

    public UserDetailService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserDetails user = userRepo.findById(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return User.withUsername(user.getUsername()).password(user.getPassword()).build();
    }
}
