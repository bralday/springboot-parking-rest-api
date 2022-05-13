package com.springboot.parking.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

@Entity
@Table(name = "tickets")
public class Ticket {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @Column(name = "licenseNumber", nullable = false)
    private String licenseNumber;

    @Column(name = "vehicleSize", nullable = false)
    private String vehicleSize;

    @Column(name = "entryPoint", nullable = false)
    private String entryPoint;

    @Column(name = "timeIn", nullable = true)
    private LocalDateTime timeIn;

    @Column(name = "timeOut", nullable = true)
    private LocalDateTime timeOut;

    @Column(name = "amountDue", nullable = true)
    private BigDecimal amountDue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slot_id", nullable = false)
    private Slot slot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parking_lot_id", nullable = false)
    private ParkingLot parkingLot;


}
