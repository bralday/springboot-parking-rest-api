package com.springboot.parking.service;

import com.springboot.parking.payload.ParkingLotDto;

import java.util.List;

public interface ParkingLotService {
    ParkingLotDto createParkingLot(ParkingLotDto parkingLotDto);

    List<ParkingLotDto> getAllParkingLots();

    ParkingLotDto getParkingLotById(long id);

    ParkingLotDto updateParkingLot(ParkingLotDto parkingLotDto, long id);

    void deleteParkingLot(long id);

}
