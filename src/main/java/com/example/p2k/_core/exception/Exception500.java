package com.example.p2k._core.exception;

import org.springframework.http.HttpStatus;

public class Exception500 extends RuntimeException{

    public Exception500(String message) {
        super(message);
    }

    public HttpStatus status(){
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}