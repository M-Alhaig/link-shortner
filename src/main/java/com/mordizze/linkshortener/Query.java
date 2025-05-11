package com.mordizze.linkshortener;


public interface Query<I, O> {
    O execute(I input);
}
