package com.ride_share.playoads;

import java.time.LocalDateTime;


import lombok.Data;
@Data
public class RideCountDto {

	 private Integer rideCountId;
	    private CategoryDto category;

	 private Integer totalRide;
	 
	    private UserDto user;
	 
	 private LocalDateTime date;
	 
}
