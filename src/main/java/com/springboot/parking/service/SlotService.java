package com.springboot.parking.service;

import com.springboot.parking.payload.ParkingLotDto;
import com.springboot.parking.payload.SlotDto;

import java.util.List;

public interface SlotService {
    SlotDto createSlot(long parkingLotId, SlotDto slotDto);

    List<SlotDto> getAllSots(long parkingLotId);

    SlotDto getSlotById(long parkingLotId, long slotId);

    SlotDto updateSlot(SlotDto slotDto, long parkingLotId, long slotId);

    void deleteSlot(long parkingLotId, long slotId);

}
