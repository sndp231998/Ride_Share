package com.ride_share.playoads;

import java.time.LocalDateTime;


import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
public class RideRequestDto {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int rideRequestId;
	
	
	private double actualPrice=0;
	
	private String source;
	//private String destination;
	private Double destination_long;
	private Double destination_lati;
	private LocalDateTime addedDate;
    private UserDto user;
   

// // Users
//    private UserDto requestedByUser; // The one who created the request
//    private UserDto approvedByUser;  // The one who approved the request

    //vehicle
    //private VehicleDto vehicle;
    
    private RideStatus status;
    
    public enum RideStatus {
    	PENDING,PESSENGER_APPROVED, REJECTED
    }
    //private Set<User> Reqriders = new HashSet<>(); // Riders who have sent requests
  //------------------------------------------------------
  	
  	private String ridebookedId;
  	
  	private CategoryDto category;

}
