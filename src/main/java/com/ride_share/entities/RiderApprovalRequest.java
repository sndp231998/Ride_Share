package com.ride_share.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class RiderApprovalRequest {
	 @Id
	    @GeneratedValue(strategy = GenerationType.AUTO)
	    private int id;

	  @ManyToOne
	    @JoinColumn(name = "user_id")
	    private User user;
	  
	  @ManyToOne
	  @JoinColumn(name="rideRequestId")
	  private RideRequest rideRequest;
	  
	  private double proposed_price;
	  
	  private Boolean isAcceptedByPassenger;
	  
}
