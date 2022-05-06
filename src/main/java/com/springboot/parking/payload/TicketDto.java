package com.springboot.parking.payload;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TicketDto {
    private Long id;
    private String licenseNumber;
    private String vehicleSize;
    private String entryPoint;
    private LocalDateTime timeIn;
    private LocalDateTime timeOut;
    private BigDecimal amountDue;
}
