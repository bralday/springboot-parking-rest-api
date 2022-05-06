package com.springboot.parking.service;

import com.springboot.parking.payload.ParkingLotDto;
import com.springboot.parking.payload.TicketDto;

import java.util.List;

public interface TicketService {

    TicketDto park(TicketDto ticketDto);

    TicketDto unpark(long ticketId);
}
