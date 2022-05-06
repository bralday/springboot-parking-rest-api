package com.springboot.parking.service;

import com.springboot.parking.payload.ParkingLotDto;
import com.springboot.parking.payload.TicketDto;
import com.springboot.parking.payload.VehicleDto;

import java.util.List;

public interface TicketService {

    TicketDto park(VehicleDto vehicleDto);

    TicketDto unpark(long ticketId);
}
