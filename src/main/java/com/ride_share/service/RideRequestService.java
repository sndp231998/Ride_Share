package com.ride_share.service;

import java.util.List;
import java.util.Set;

import com.ride_share.playoads.PriceInfoDto;
import com.ride_share.playoads.RideRequestDto;
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

	//RideRequestDto approveRideRequest(Integer riderId);

	RideRequestDto approveRideRequestByRider(RideRequestDto rideRequestDto, Integer userId, Integer rideRequestId);

	//RideRequestDto approveRideRequestByPassenger(Integer rideRequestId);

	//void sendRideRequest(Integer rideRequestId, Integer riderId);

	Set<UserDto> getRidersForRideRequest(Integer rideRequestId);

	
	RideRequestDto approveRideRequestByPassenger(Integer rideRequestId, Integer userId, Integer currentUserId);

	PriceInfoDto determinePrice(RideRequestDto rideRequestDto, Integer userId, Integer categoryId);

	
	

}
