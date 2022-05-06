package com.springboot.parking.service.impl;

import com.springboot.parking.entity.ParkingLot;
import com.springboot.parking.exception.ResourceNotFoundException;
import com.springboot.parking.payload.ParkingLotDto;
import com.springboot.parking.repository.ParkingLotRepository;
import com.springboot.parking.service.ParkingLotService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParkingLotServiceImpl implements ParkingLotService {

    private ModelMapper mapper;

    private ParkingLotRepository parkingLotRepository;

    public ParkingLotServiceImpl(ParkingLotRepository parkingLotRepository, ModelMapper mapper) {
        this.parkingLotRepository = parkingLotRepository;
        this.mapper = mapper;
    }

    @Override
    public ParkingLotDto createParkingLot(ParkingLotDto parkingLotDto) {
        // Convert DTO to entity using private method below.
        ParkingLot parkingLot = mapToEntity(parkingLotDto);

        // Save to database using .save()
        ParkingLot newParkingLot = this.parkingLotRepository.save(parkingLot);

        //convert entity (post) to DTO using private method below.
        ParkingLotDto parkingLotResponse = mapToDto(newParkingLot);

        return parkingLotResponse;
    }

    @Override
    public List<ParkingLotDto> getAllParkingLots() {
       List<ParkingLot> parkingLots =  parkingLotRepository.findAll();

       return parkingLots.stream().map(parkingLot -> mapToDto(parkingLot)).collect(Collectors.toList());
    }

    @Override
    public ParkingLotDto getParkingLotById(long id) {
        ParkingLot parkingLot = parkingLotRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Parking Lot", "ID", id));
        ParkingLotDto responseDto = mapToDto(parkingLot);

        return responseDto;
    }

    @Override
    public ParkingLotDto updateParkingLot(ParkingLotDto parkingLotDto, long id) {
        ParkingLot parkingLot = parkingLotRepository.findById(id).orElseThrow(() ->new ResourceNotFoundException("Parking Lot", "ID", id));

        parkingLot.setLocation(parkingLotDto.getLocation());
        parkingLot.setLotName(parkingLotDto.getLotName());

        // Save to database using .save()
        // IMPORTANT OR ELSE IT WONT REFLECT SA GETMAPPING
        ParkingLot newParkingLot = this.parkingLotRepository.save(parkingLot);
        return mapToDto(newParkingLot);
    }

    @Override
    public void deleteParkingLot(long id) {
        boolean b = parkingLotRepository.existsById(id);

        if(!b){
            throw new ResourceNotFoundException("Post", "ID", id);
        }else{
            parkingLotRepository.deleteById(id);
        }
    }

    // Convert DTO to entity
    private ParkingLot mapToEntity (ParkingLotDto parkingLotDto){

        ParkingLot parkingLot = mapper.map(parkingLotDto, ParkingLot.class);

        return parkingLot;
    }

    //convert entity (post) to DTO
    private ParkingLotDto mapToDto(ParkingLot parkingLot){

        ParkingLotDto parkingLotDto = mapper.map(parkingLot, ParkingLotDto.class);

        return parkingLotDto;
    }




}
