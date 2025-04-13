package com.ride_share.playoads;

import java.time.LocalDateTime;


import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideRequestDto {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int rideRequestId;
	
	
	private double actualPrice=0;
	
	private double d_latitude;
    private double d_longitude;
    private String d_Name;
    private double s_latitude;
    private double s_longitude;
    private String s_Name;
    private double total_Km;
    private int approvedriderId;
	private LocalDateTime addedDate;
    private UserDto user;
    private double replacePessengerPrice=0.0;
    private double generatedPrice;
// // Users
//    private UserDto requestedByUser; // The one who created the request
//    private UserDto approvedByUser;  // The one who approved the request

    //vehicle
   // private VehicleDto vehicle;
    
    private RideStatus status;
    
    public enum RideStatus {
    	PENDING,PESSENGER_APPROVED, REJECTED
    }
    //private Set<User> Reqriders = new HashSet<>(); // Riders who have sent requests
  //------------------------------------------------------
  	
  	private String ridebookedId;
  	
  	private CategoryDto category;

}
