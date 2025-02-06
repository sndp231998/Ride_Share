package com.ride_share.playoads;

import java.time.LocalDateTime;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.ride_share.entities.RideRequest;
import com.ride_share.entities.User;
import com.ride_share.entities.RideRequest.RideStatus;

import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
public class RideRequestDto {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int rideRequestId;
	
	
	private String actualPrice;
	
	private String source;
	private String destination;
	
	private LocalDateTime addedDate;
    private User user;
    
    
    private RideStatus status;
    
    public enum RideStatus {
        PENDING, APPROVED, REJECTED
    }

}
