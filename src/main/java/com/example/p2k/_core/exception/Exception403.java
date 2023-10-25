package com.example.p2k._core.exception;

import org.springframework.http.HttpStatus;

public class Exception403 extends RuntimeException{

    public Exception403(String message) {
        super(message);
    }

    public HttpStatus status(){
        return HttpStatus.FORBIDDEN;
    }
}