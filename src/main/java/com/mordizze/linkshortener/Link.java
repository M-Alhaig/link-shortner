package com.mordizze.linkshortener;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "link")
public class Link {
    @Id
    String shortCode;

    String originalUrl;
    
    int clickCount;
    
    @CreationTimestamp
    Date createdAt;
}
