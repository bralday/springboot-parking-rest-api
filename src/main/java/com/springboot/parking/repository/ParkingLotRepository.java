package com.springboot.parking.repository;

import com.springboot.parking.entity.ParkingLot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingLotRepository extends JpaRepository<ParkingLot, Long > {
}
