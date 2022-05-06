package com.springboot.parking.controller;


import com.springboot.parking.payload.SlotDto;
import com.springboot.parking.service.ParkingLotService;
import com.springboot.parking.service.SlotService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/")
public class SlotController {

    private SlotService slotService;

    private ParkingLotService parkingLotService;

    public SlotController(SlotService slotService, ParkingLotService parkingLotService) {
        this.slotService = slotService;
        this.parkingLotService = parkingLotService;
    }

    // Create new slot
    @PostMapping("/parkinglots/{parkingLotId}/slots")
    public ResponseEntity<SlotDto> createSlot(
            @PathVariable(value = "parkingLotId") long parkingLotId,
            @RequestBody SlotDto slotDto){
        return new ResponseEntity<>(slotService.createSlot(parkingLotId,slotDto), HttpStatus.CREATED);
    }

    // Get all slots of parkinglot
    @GetMapping("/parkinglots/{parkingLotId}/slots")
    public List<SlotDto> getAllSots(
            @PathVariable(value = "parkingLotId") long parkingLotId
    ){
        return slotService.getAllSots(parkingLotId);
    }

    // Get specific slot by id
    @GetMapping("/parkinglots/{parkingLotId}/slots/{id}")
    public ResponseEntity<SlotDto> getSlotById(
            @PathVariable(value = "parkingLotId") long parkingLotId,
            @PathVariable(value = "id") long id
    ){
        return new ResponseEntity<>(slotService.getSlotById(parkingLotId,id),HttpStatus.OK);
    }

    // Update slot
    @PutMapping("/parkinglots/{parkingLotId}/slots/{id}")
    public ResponseEntity<SlotDto> updateSlot(
            @PathVariable(value = "parkingLotId") long parkingLotId,
            @PathVariable(value = "id") long id,
            @RequestBody SlotDto slotDto
    ){
        return new ResponseEntity<>(slotService.updateSlot(slotDto,parkingLotId,id), HttpStatus.OK);
    }

    // Delete comment by id
    @DeleteMapping("/parkinglots/{parkingLotId}/slots/{id}")
    public ResponseEntity<String> deleteComment(
            @PathVariable(value = "parkingLotId") long parkingLotId,
            @PathVariable(value = "id") long id){
        slotService.deleteSlot(parkingLotId,id);

        return new ResponseEntity<>("Slot has been deleted successfully!", HttpStatus.OK);
    }
}
