package com.ride_share.entities;



import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
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
	
	//ui bata malai destination of long ra lati chinxa
	private Double destination_long;
	private Double destination_lati;
	
	private LocalDateTime addedDate;
	@ManyToOne
    @JoinColumn(name = "user_id")// The passenger who created the request pessenger Id
    private User user;
	
	 @ManyToOne
	    @JoinColumn(name = "category_id")
	    private Category category;
	
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
	        PENDING,PESSENGER_APPROVED, REJECTED
	    }
	
//------------------------------------------------------
	@Column(name="approved_user_Id")// after pessenger choosed rider, storing userId
	private String ridebookedId;
	
	

	

}
