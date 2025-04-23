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

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//@Table(
//	    name = "rider_approval_request",
//	    uniqueConstraints = @UniqueConstraint(columnNames = {"ride_request_id", "user_id"})
//	)

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class RiderApprovalRequest {
	 @Id
	    @GeneratedValue(strategy = GenerationType.AUTO)
	    private int id;

	  @ManyToOne
	    @JoinColumn(name = "user_id")
	    private User user;
	  
	  @ManyToOne
	    @JoinColumn(name = "ride_request_id") // Fixed column name
	  @JsonBackReference
	    private RideRequest rideRequest;
       private double minToReach;
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