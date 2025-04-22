package com.ride_share.playoads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideRequestResponseDto {
	 private Integer userId;
	 private String name;
	    private String mobileNo;
	   // private String vehicle;
	    private double minToReach;
	    private String vehicleBrand;  // Add vehicle brand
	    private Double proposedPrice; 
	    private String vehicleType;   // Add vehicle type
	    
	    private String vehicleNumber;
	    private LocationDto currentLocation;
	    
	    
	    
	    @Data
	    @NoArgsConstructor
	    public class LocationDto {
	        private Double latitude;
	        private Double longitude;
	    }
}
