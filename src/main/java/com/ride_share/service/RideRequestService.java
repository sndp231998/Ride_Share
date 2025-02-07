package com.ride_share.service;

import java.util.List;

import com.ride_share.playoads.RideRequestDto;


public interface RideRequestService {

	RideRequestDto createRideRequest(RideRequestDto rideRequestDto,Integer userId);

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

	RideRequestDto approveRideRequestByPassenger(Integer rideRequestId);
	

}
