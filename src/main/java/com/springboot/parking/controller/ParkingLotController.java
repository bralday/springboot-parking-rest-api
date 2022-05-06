package com.springboot.parking.controller;

import com.springboot.parking.payload.ParkingLotDto;
import com.springboot.parking.service.ParkingLotService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parkinglots")
public class ParkingLotController {

    private ParkingLotService parkingLotService;

    public ParkingLotController(ParkingLotService parkingLotService) {
        this.parkingLotService = parkingLotService;
    }

    @PostMapping
    public ResponseEntity<ParkingLotDto> createPost(@RequestBody ParkingLotDto parkingLotDto){ // @RequestBody converts JSON to a java object
        return new ResponseEntity<>(parkingLotService.createParkingLot(parkingLotDto), HttpStatus.CREATED);
    }

    @GetMapping
    public List<ParkingLotDto> getAllParkingLots(){
        return parkingLotService.getAllParkingLots();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParkingLotDto> getPostById(@PathVariable(name = "id") long id){
        return ResponseEntity.ok(parkingLotService.getParkingLotById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParkingLotDto> updateParkingLot(@RequestBody ParkingLotDto parkingLotDto, @PathVariable(name = "id") long id){
        ParkingLotDto parkingLotResponse = parkingLotService.updateParkingLot(parkingLotDto, id);

        return new ResponseEntity<>(parkingLotResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteParkingLot(@PathVariable(name = "id") long id){
        parkingLotService.deleteParkingLot(id);

        return new ResponseEntity<>("Parking Lot has been deleted successfully!", HttpStatus.OK);
    }
}
