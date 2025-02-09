package com.ride_share.entities;



import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;



import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class RideRequest {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int rideRequestId;
	
	
	private String actualPrice;
	
	private String source;
	private String destination;
	private LocalDateTime addedDate;
	@ManyToOne
    @JoinColumn(name = "user_id")// The passenger who created the request
    private User user;
	
	
	@ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "ride_request_riders", // Join table name
        joinColumns = @JoinColumn(name = "ride_request_id"), // Column for RideRequest
        inverseJoinColumns = @JoinColumn(name = "user_id") // Column for User (rider)
    )
    private Set<User> Reqriders = new HashSet<>(); // Riders who have sent requests

	
	
	 @Enumerated(EnumType.STRING)
	    private RideStatus status;
	    
	    public enum RideStatus {
	        PENDING, RIDER_APPROVED,PESSENGER_PAPPROVED, REJECTED
	    }

	

}
