package com.mordizze.linkshortener.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.extern.slf4j.Slf4j;

@ResponseStatus(HttpStatus.BAD_REQUEST)
@Slf4j
public class InvalidIntervalException extends RuntimeException{

    public InvalidIntervalException() {
        super("Invalid Interval Entered, Available Intervals: ('Daily', 'Weekly')");
        log.error("Exception Thrown {}", getClass());
    }
}
