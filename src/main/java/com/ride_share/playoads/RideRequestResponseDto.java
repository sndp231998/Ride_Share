package com.ride_share.playoads;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideRequestResponseDto {
	 private Integer userId;
	 private String name;
	    private String mobileNo;
	   // private String vehicle;
	    private double minToReach;
	    private String vehicleBrand;  // Add vehicle brand
	    private Double proposedPrice; 
	    private String vehicleType;   // Add vehicle type
	    private int rideRequestId;
	    private int Id;//RiderApprovalRequestId
	    private String vehicleNumber;
	    private String riderImage;
	    private Double averageRating;
	    private Double riderLati;
	    private Double riderLong;


	
}
