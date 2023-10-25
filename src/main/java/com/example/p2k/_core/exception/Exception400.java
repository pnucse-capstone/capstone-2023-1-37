package com.example.p2k._core.exception;

import org.springframework.http.HttpStatus;

public class Exception400 extends RuntimeException{

    public Exception400(String message) {
        super(message);
    }

    public HttpStatus status(){
        return HttpStatus.BAD_REQUEST;
    }
}