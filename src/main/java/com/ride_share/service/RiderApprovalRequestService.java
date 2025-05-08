package com.ride_share.service;

import java.util.Set;


import com.ride_share.playoads.RideRequestResponseDto;
import com.ride_share.playoads.RiderApprovalRequestDto;


public interface RiderApprovalRequestService {

	RiderApprovalRequestDto createRiderApproval(RiderApprovalRequestDto riderApprovalRequestDto, Integer rideRequestId, Integer userId);

	Set<RideRequestResponseDto> getRidersForRideRequest(Integer rideRequestId);

	RiderApprovalRequestDto rejectRideApproval(Integer riderAppId);

	
	

	
}
