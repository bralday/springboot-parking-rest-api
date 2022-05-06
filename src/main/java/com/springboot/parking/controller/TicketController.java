package com.springboot.parking.controller;

import com.springboot.parking.payload.ParkingLotDto;
import com.springboot.parking.payload.TicketDto;
import com.springboot.parking.service.ParkingLotService;
import com.springboot.parking.service.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<TicketDto> park(@RequestBody TicketDto ticketDto){ // @RequestBody converts JSON to a java object
        return new ResponseEntity<>(ticketService.park(ticketDto), HttpStatus.OK);
    }


    @PutMapping("/{id}")
    public ResponseEntity<TicketDto> updateParkingLot(@PathVariable(name = "id") long id){
        TicketDto ticketDto = ticketService.unpark(id);

        return new ResponseEntity<>(ticketDto, HttpStatus.OK);
    }

}
