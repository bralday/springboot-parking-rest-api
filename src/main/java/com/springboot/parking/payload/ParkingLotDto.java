package com.springboot.parking.payload;

import com.springboot.parking.entity.Slot;
import com.springboot.parking.entity.Ticket;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class ParkingLotDto {
    private Long id;
    private String lotName;
    private String location;
    private Set<SlotDto> slots = new HashSet<>();
    private Set<TicketDto> tickets = new HashSet<>();
}
