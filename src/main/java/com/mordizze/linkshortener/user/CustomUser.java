package com.mordizze.linkshortener.user;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "users")
public class CustomUser {

    @Id
    private String username;

    private String password;
}