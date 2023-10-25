package com.example.p2k._core.exception;

import org.springframework.http.HttpStatus;

public class Exception404 extends RuntimeException{

    public Exception404(String message) {
        super(message);
    }

    public HttpStatus status(){
        return HttpStatus.NOT_FOUND;
    }
}