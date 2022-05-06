package com.springboot.parking.service.impl;

import com.springboot.parking.entity.ParkingLot;
import com.springboot.parking.entity.Slot;
import com.springboot.parking.exception.ParkingAPIException;
import com.springboot.parking.exception.ResourceNotFoundException;
import com.springboot.parking.payload.SlotDto;
import com.springboot.parking.repository.ParkingLotRepository;
import com.springboot.parking.repository.SlotRepository;
import com.springboot.parking.service.SlotService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SlotServiceImpl implements SlotService {

    private SlotRepository slotRepository;
    private ParkingLotRepository parkingLotRepository;
    private ModelMapper mapper;

    public SlotServiceImpl(SlotRepository slotRepository, ParkingLotRepository parkingLotRepository, ModelMapper mapper) {
        this.slotRepository = slotRepository;
        this.parkingLotRepository = parkingLotRepository;
        this.mapper = mapper;
    }


    @Override
    public SlotDto createSlot(long parkingLotId, SlotDto slotDto) {
        // Convert DTO to entity using private method below.
        Slot slot = mapToEntity(slotDto);

        // Retrieve post entity by id
        ParkingLot parkingLot = parkingLotRepository.findById(parkingLotId).orElseThrow(
                () -> new ResourceNotFoundException("Parking Lot", "id", parkingLotId));

        //setPost to comment entity
        slot.setParkingLot(parkingLot);

        // Save to database using .save()
        Slot newSlot = this.slotRepository.save(slot);

        //convert entity (post) to DTO using private method below.
        return mapToDto(newSlot);
    }

    @Override
    public List<SlotDto> getAllSots(long parkingLotId) {
        // retrieve comments by postId
        List<Slot> slots = slotRepository.findByParkingLotId(parkingLotId);

        return slots.stream().map(slot -> mapToDto(slot)).collect(Collectors.toList());
    }

    @Override
    public SlotDto getSlotById(long parkingLotId, long slotId) {
        // Retrieve parking lot entity by id
        ParkingLot parkingLot = parkingLotRepository.findById(parkingLotId).orElseThrow(
                () -> new ResourceNotFoundException("Parking Lot", "id", parkingLotId));

        // Retrieve slot entity by id
        Slot slot = slotRepository.findById(slotId).orElseThrow(
                () -> new ResourceNotFoundException("Slot", "id", slotId));

        // Check if slot belongs to the parkinglot, return exception if not.
        if(!slot.getParkingLot().getId().equals(parkingLotId)){
            throw new ParkingAPIException(HttpStatus.BAD_REQUEST,"Slot does not belong to this parking lot" );
        }

        SlotDto responseDto = mapToDto(slot);
        return responseDto;
    }

    @Override
    public SlotDto updateSlot(SlotDto slotDto, long parkingLotId, long slotId) {
        // Retrieve parkinglot entity by id
        ParkingLot parkingLot = parkingLotRepository.findById(parkingLotId).orElseThrow(
                () -> new ResourceNotFoundException("Parking Lot", "id", parkingLotId));

        // Retrieve slot entity by id
        Slot slot = slotRepository.findById(slotId).orElseThrow(
                () -> new ResourceNotFoundException("Slot", "id", slotId));

        // Check if slot belongs to the parking lot, return exception if not.
        if(!slot.getParkingLot().getId().equals(parkingLotId)){
            throw new ParkingAPIException(HttpStatus.BAD_REQUEST,"Slot does not belong to this parking lot" );
        }

        slot.setSlotSize(slotDto.getSlotSize());
        slot.setPerHour(slotDto.getPerHour());
        slot.setFlatRate(slotDto.getPerHour());
        slot.setEntryPoint(slotDto.getEntryPoint());

        //save comment (will save to db too)
        Slot updatedSlot = slotRepository.save(slot);

        return mapToDto(updatedSlot);
    }

    @Override
    public void deleteSlot(long parkingLotId, long slotId) {
        // Retrieve parkinglot entity by id
        ParkingLot parkingLot = parkingLotRepository.findById(parkingLotId).orElseThrow(
                () -> new ResourceNotFoundException("Parking Lot", "id", parkingLotId));

        // Retrieve slot entity by id
        Slot slot = slotRepository.findById(slotId).orElseThrow(
                () -> new ResourceNotFoundException("Slot", "id", slotId));

        // Check if slot belongs to the parking lot, return exception if not.
        if(!slot.getParkingLot().getId().equals(parkingLotId)){
            throw new ParkingAPIException(HttpStatus.BAD_REQUEST,"Slot does not belong to this parking lot" );
        }

        slotRepository.deleteById(slotId);
    }

    // Convert DTO to entity
    private Slot mapToEntity (SlotDto slotDto){

        Slot slot = mapper.map(slotDto, Slot.class);

        return slot;
    }

    //convert entity (post) to DTO
    private SlotDto mapToDto(Slot slot){

        SlotDto slotDto = mapper.map(slot, SlotDto.class);

        return slotDto;
    }
}
