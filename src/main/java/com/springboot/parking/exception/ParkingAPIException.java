package com.springboot.parking.exception;

import org.springframework.http.HttpStatus;

public class ParkingAPIException extends RuntimeException {

    private HttpStatus httpStatus;
    private String message;

    public ParkingAPIException(String message, HttpStatus httpStatus, String message1) {
        this.httpStatus = httpStatus;
        this.message = message1;
    }

    public ParkingAPIException(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}


