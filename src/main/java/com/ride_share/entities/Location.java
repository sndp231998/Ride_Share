package com.ride_share.entities;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.ride_share.playoads.UserDto;

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
    private LocalDateTime timestamp;
    
 // Destination Coordinates (Drop-off Location)
    private Double destinationLatitude;
    private Double destinationLongitude;
    
   

    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

}
