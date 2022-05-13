package com.springboot.parking.repository;


import com.springboot.parking.entity.Slot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SlotRepository extends JpaRepository<Slot, Long > {

    List<Slot> findByParkingLotId(long parkingLotId);
    List<Slot> findByEntryPointAndSlotSize(String entryPoint, String size);
    List<Slot> findBySlotSize(String size);
}
