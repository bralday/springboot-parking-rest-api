package com.springboot.parking.payload;


import lombok.Data;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Data
public class SlotDto {

    private Long id;
    private String entryPoint;
    private String slotSize;
    private BigDecimal flatRate;
    private BigDecimal perHour;
    private boolean isOccupied;
    private Set<TicketDto> tickets = new HashSet<>();
}
