package com.springboot.parking.repository;

import com.springboot.parking.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long > {
    Boolean existsByLicenseNumber(String license_number);
    Vehicle findByLicenseNumber (String license_number);


}
