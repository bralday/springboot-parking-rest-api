package com.springboot.parking.repository;

import com.springboot.parking.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByParkingLotId(long parkingLotId);
    List<Ticket> findByLicenseNumber(String license_number);
    Boolean existsByLicenseNumber(String license_number);
}
