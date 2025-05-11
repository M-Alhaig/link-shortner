package com.mordizze.linkshortener.models;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data   
@AllArgsConstructor
public class RedirectRequest {

    private String shortCode;
    private HttpServletRequest request;
}
