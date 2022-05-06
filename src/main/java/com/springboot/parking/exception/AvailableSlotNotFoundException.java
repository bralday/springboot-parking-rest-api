package com.springboot.parking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND) // Returns a specific status code
public class AvailableSlotNotFoundException extends RuntimeException{


    public AvailableSlotNotFoundException(){
        super(String.format("No available parking slot found."));

    }

}