package com.ride_share.playoads;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.ride_share.entities.RideRequest;
import com.ride_share.entities.RiderApprovalRequest;
import com.ride_share.entities.User;
import com.ride_share.entities.RideRequest.RideStatus;
import com.ride_share.entities.RiderApprovalRequest.ApprovedStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiderApprovalRequestDto {

	 @Id
	    @GeneratedValue(strategy = GenerationType.AUTO)
	    private int id;
	    private UserDto user;
	  private RideRequestDto rideRequest;
	  
	  private double proposed_price;
	  private ApprovedStatus status;
	  private LocalDateTime addedDate;



}
	  

