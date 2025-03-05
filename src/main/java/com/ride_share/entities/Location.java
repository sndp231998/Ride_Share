package com.ride_share.entities;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

 // Source Coordinates (Pickup Location)
    private Double latitude;
    private Double longitude;
    
    
 // Destination Coordinates (Drop-off Location)
    private Double destinationLatitude;
    private Double destinationLongitude;
    
    private LocalDateTime timestamp;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

}
