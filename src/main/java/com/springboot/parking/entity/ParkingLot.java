package com.springboot.parking.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

//@Data // Lombok - Generates getters, setters, and toString (via annotations)
@AllArgsConstructor // Generates all args constructor via Lombok
@NoArgsConstructor // Generates no args constructor via Lombok
@Getter // added these 2 instead of @Data to prevent stackoverflow error caused by toString()
@Setter

@Entity
@Table(
        name = "parkinglots",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"lotname"})}
)
public class ParkingLot {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @Column(name = "lotname", nullable = false)
    private String lotName;

    @Column(name = "location", nullable = false)
    private String location;

    @OneToMany(mappedBy = "parkingLot", cascade = CascadeType.ALL, orphanRemoval = true) // post field in Comment.class
    private Set<Slot> slots = new HashSet<>();

    // to add slot field

}
