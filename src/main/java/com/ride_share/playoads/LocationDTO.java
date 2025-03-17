package com.ride_share.playoads;

import java.time.LocalDateTime;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.ride_share.entities.User;

import lombok.Data;
@Data
public class LocationDTO {

	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    private Double latitude;
	    private Double longitude;
	    private LocalDateTime timestamp;

	    // Destination Coordinates (Drop-off Location)
	    private Double destinationLatitude;
	    private Double destinationLongitude;
	    
	   
	   
	    private User user;
}
