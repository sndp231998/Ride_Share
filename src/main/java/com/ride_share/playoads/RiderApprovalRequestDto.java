package com.ride_share.playoads;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class RiderApprovalRequestDto {

	 @Id
	    @GeneratedValue(strategy = GenerationType.AUTO)
	    private int id;

	  
	    private UserDto user;
	  
	 
	  private RideRequestDto rideRequest;
	  
	  private double proposed_price;
	  
	  private Boolean isAcceptedByPassenger;
	  
}
