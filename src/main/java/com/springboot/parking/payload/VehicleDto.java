package com.springboot.parking.payload;

import com.springboot.parking.entity.Ticket;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
public class VehicleDto {

    private Long id;
    private String licenseNumber;
    private String vehicleSize;
    private String entryPoint;
    private Set<Ticket> tickets = new HashSet<>();

}
