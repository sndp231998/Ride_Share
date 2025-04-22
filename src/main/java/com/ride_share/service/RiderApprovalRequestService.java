package com.ride_share.service;

import java.util.List;

import com.ride_share.entities.RiderApprovalRequest;
import com.ride_share.playoads.RiderApprovalRequestDto;

public interface RiderApprovalRequestService {

	RiderApprovalRequestDto createRiderApproval(RiderApprovalRequestDto riderApprovalRequestDto, Integer rideRequestId, Integer userId);

	//RiderApprovalRequestDto createRiderApproval(RiderApprovalRequestDto riderApprovalRequestDto, Integer rideRequestId);
	// List<RiderApprovalRequest> findByRideRequest_RideRequestIdAndStatus(int rideRequestId, RiderApprovalRequest.ApprovedStatus status);
	

	//void deleteComment(Integer commentId);

	
}
