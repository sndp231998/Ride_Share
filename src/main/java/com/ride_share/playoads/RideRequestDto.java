package com.ride_share.playoads;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.ride_share.entities.RiderApprovalRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideRequestDto {

//	@Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    private int rideRequestId;
	
    @Builder.Default
	private double actualPrice=0.0;
	
	private double d_latitude;
    private double d_longitude;
    private String d_Name;
    private double s_latitude;
    private double s_longitude;
    private String s_Name;
    private double total_Km;
    
	private LocalDateTime addedDate;
    private UserDto user;
    @Builder.Default
    private double replacePessengerPrice=0.0;
    private double generatedPrice;
    //private Set<RiderApprovalRequestDto> riderApprovalRequests=new HashSet<>();
    private Set<Integer> riderApprovalRequestIds; // just IDs or lightweight structure

    private RideStatus status;
    
    public enum RideStatus {
    	PENDING,PESSENGER_APPROVED, REJECTED
    }

  	
  	private Integer ridebookedId;
  	
  	private CategoryDto category;

}
