package com.springboot.parking.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor // Generates all args constructor via Lombok
@NoArgsConstructor // Generates no args constructor via Lombok
@Getter // added these 2 instead of @Data to prevent stackoverflow error caused by toString()
@Setter

@Entity
@Table(name = "vehicles")
public class Vehicle {
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

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Ticket> tickets = new HashSet<>();

}
