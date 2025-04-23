package com.ride_share.service;

import java.util.List;
import java.util.Set;

import com.ride_share.entities.RiderApprovalRequest;
import com.ride_share.playoads.RideRequestDto;
import com.ride_share.playoads.RideRequestResponseDto;
import com.ride_share.playoads.RiderApprovalRequestDto;
import com.ride_share.playoads.UserDto;

public interface RiderApprovalRequestService {

	RiderApprovalRequestDto createRiderApproval(RiderApprovalRequestDto riderApprovalRequestDto, Integer rideRequestId, Integer userId);

	Set<RideRequestResponseDto> getRidersForRideRequest(Integer rideRequestId);

	//RideRequestDto approveRideRequestByPassenger(RiderApprovalRequestDto riderApprovalRequestDto, Integer Id, Integer rideRequestId);



	//Set<UserDto> getRidersForRideRequest(Integer rideRequestId);
	//RiderApprovalRequestDto createRiderApproval(RiderApprovalRequestDto riderApprovalRequestDto, Integer rideRequestId);
	// List<RiderApprovalRequest> findByRideRequest_RideRequestIdAndStatus(int rideRequestId, RiderApprovalRequest.ApprovedStatus status);
	

	//void deleteComment(Integer commentId);

	
}
