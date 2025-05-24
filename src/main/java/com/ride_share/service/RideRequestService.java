package com.ride_share.service;

import java.util.List;
import java.util.Set;

import com.ride_share.entities.RideRequest;
import com.ride_share.playoads.PriceInfoDto;
import com.ride_share.playoads.RideInfoDto;
import com.ride_share.playoads.RideRequestDto;
import com.ride_share.playoads.RiderApprovalRequestDto;
import com.ride_share.playoads.UserDto;


public interface RideRequestService {

	
	RideRequestDto createRideRequest(RideRequestDto rideRequestDto, Integer userId, Integer categoryId);

	//update 

	RideRequestDto updateRideRequest(RideRequestDto rideRequestDto, Integer rideRequestId);

	// delete
	void deleteRideRequest(Integer rideRequestId);

	RideRequestDto getRideRequestById(Integer rideRequestId);
	
	List<RideRequestDto> getRideRequestByUser(Integer userId);

	List<RideRequestDto> getAllRideRequests();


	RideRequestDto rejectRideRequest(Integer rideRequestId);

	RideRequestDto approveRideRequestByRider(RideRequestDto rideRequestDto, Integer userId, Integer rideRequestId);


	//RideRequestDto approveRideRequestByPassenger(Integer rideRequestId, Integer userId, Integer currentUserId);

	PriceInfoDto determinePrice(RideRequestDto rideRequestDto, Integer userId, Integer categoryId);

	RideInfoDto detailrideViewByRider(RideRequestDto rideRequestDto, Integer rideRequestId, Integer userId);

	
	RideRequestDto approveRideRequestByPassenger(Integer Id, Integer rideRequestId);

	List<RideRequestDto> getRideRequestsByUserCategory(int userId);

	List<RideRequestDto> getSortedPendingRideRequests(int riderUserId);

	RideRequestDto rideComplete(Integer rideRequestId);

	RideRequestDto ridePickup(Integer rideRequestId);

	List<RideRequestDto> getRequestsByUserId(Integer userId);

	List<RideRequestDto> getRequestsByRiderId(Integer userId);


}
