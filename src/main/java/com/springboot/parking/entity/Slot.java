package com.springboot.parking.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor // Generates all args constructor via Lombok
@NoArgsConstructor // Generates no args constructor via Lombok
@Getter // added these 2 instead of @Data to prevent stackoverflow error caused by toString()
@Setter

@Entity
@Table(name = "slots")
public class Slot {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @Column(name = "entryPoint", nullable = false)
	private String entryPoint;

    @Column(name = "slotSize", nullable = false)
    private String slotSize;

    @Column(name = "flatRate", nullable = false)
	private BigDecimal flatRate;

    @Column(name = "perHour", nullable = false)
    private BigDecimal perHour;

    @Column(name = "occupant", nullable = true)
    private String occupant;

    @Column(name = "isOccupied", nullable = true, columnDefinition = "boolean default false")
    private boolean isOccupied;

    @OneToMany(mappedBy = "slot", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Ticket> tickets = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parking_lot_id", nullable = false)
    private ParkingLot parkingLot;

}
