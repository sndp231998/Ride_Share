package com.ride_share.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ride_share.playoads.RideRequestDto;
import com.ride_share.playoads.RideRequestResponseDto;
import com.ride_share.playoads.RiderApprovalRequestDto;
import com.ride_share.playoads.UserDto;
import com.ride_share.service.RiderApprovalRequestService;

@RestController
@RequestMapping("/api/v1/riderAppReq")
public class RiderapprovalRequestController {

	@Autowired
	RiderApprovalRequestService riderApprovalService;
	
	 @PostMapping("/{rideRequestId}/user/{userId}")
	    public ResponseEntity<RiderApprovalRequestDto> createRiderApproval(@RequestBody RiderApprovalRequestDto riderApprovalRequestDto,
	                                                    @PathVariable Integer rideRequestId,   @PathVariable Integer userId
	                                                    ) {
		 RiderApprovalRequestDto created = riderApprovalService.createRiderApproval(riderApprovalRequestDto, rideRequestId,userId);
	        return new ResponseEntity<>(created, HttpStatus.CREATED);
	    }
	
	    // Reject a ride request
	    @PutMapping("/{rideAppId}/reject")
	    public ResponseEntity<RiderApprovalRequestDto> rejectRideApproval(@PathVariable Integer riderAppId) {
	    	RiderApprovalRequestDto rejectedRequest = riderApprovalService.rejectRideApproval(riderAppId);
	        		
	        return ResponseEntity.ok(rejectedRequest);
	    }
	

	 @GetMapping("/{rideRequestId}/pending-riders")
	 public ResponseEntity<Set< RideRequestResponseDto>> getPendingRiders(@PathVariable Integer rideRequestId) {
	     Set< RideRequestResponseDto> pendingRiders = riderApprovalService.getRidersForRideRequest(rideRequestId);
	     return ResponseEntity.ok(pendingRiders);
	 }

}
