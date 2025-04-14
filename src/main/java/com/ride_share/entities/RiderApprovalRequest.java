package com.ride_share.entities;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor



public class RiderApprovalRequest {
	 @Id
	    @GeneratedValue(strategy = GenerationType.AUTO)
	    private int id;

	  @ManyToOne
	    @JoinColumn(name = "user_id")
	    private User user;
	  
	  @ManyToOne
	    @JoinColumn(name = "ride_request_id") // Fixed column name
	    private RideRequest rideRequest;
	  
	  
	  
	  private double proposed_price;
	 
	  @Enumerated(EnumType.STRING)
	    private ApprovedStatus status;
	    
	    public enum ApprovedStatus {
	        PENDING,PESSENGER_APPROVED, REJECTED
	    }
	  
	
	    private LocalDateTime addedDate;
	  
}



//@Table(
//	    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "ride_request_id"})
//	)