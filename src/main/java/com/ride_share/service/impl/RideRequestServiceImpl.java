package com.ride_share.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ride_share.entities.RideRequest;
import com.ride_share.entities.Rider;
import com.ride_share.entities.User;
import com.ride_share.exceptions.ResourceNotFoundException;
import com.ride_share.playoads.RideRequestDto;
import com.ride_share.playoads.RiderDto;
import com.ride_share.repositories.RideRequestRepo;
import com.ride_share.repositories.RiderRepo;
import com.ride_share.repositories.UserRepo;
import com.ride_share.service.RideRequestService;


@Service
public class RideRequestServiceImpl implements RideRequestService{

	
	@Autowired
	private ModelMapper modelMapper;
	@Autowired
	private RideRequestRepo rideRequestRepo;
	
	@Autowired
	private UserRepo userRepo;
	
	
	@Override
	public RideRequestDto createRideRequest(RideRequestDto rideRequestDto, Integer userId) {
		User user = this.userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "User ID", userId));

//	    Rider.RiderStatus existingStatus = this.riderRepo.findRiderStatusByUserId(userId);
//        if (existingStatus!= Rider.RiderStatus.APPROVED) {
//            throw new IllegalStateException("Cannot create Ride_Request.");
//        }
		//
        RideRequest ride = this.modelMapper.map(rideRequestDto, RideRequest.class);
		ride.setAddedDate(LocalDateTime.now());
		ride.setActualPrice(rideRequestDto.getActualPrice());
		ride.setSource(rideRequestDto.getSource());
		ride.setDestination(rideRequestDto.getDestination());
		ride.setStatus(RideRequest.RideStatus.PENDING);
        //ride.set(rideRequestDto.getActualPrice());
		RideRequest savedRideReq = this.rideRequestRepo.save(ride);
        return this.modelMapper.map(savedRideReq, RideRequestDto.class);
	}

	@Override
	public RideRequestDto updateRideRequest(RideRequestDto rideRequestDto, Integer rideRequestId) {
		RideRequest req = this.rideRequestRepo.findById(rideRequestId)
				.orElseThrow(()->new ResourceNotFoundException("RideRequest","RideRequest ID",rideRequestId));
		
		return null;
	}

	@Override
	public void deleteRideRequest(Integer rideRequestId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public RideRequestDto getRideRequestById(Integer rideRequestId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<RideRequestDto> getRideRequestByUser(Integer userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<RideRequestDto> getAllRideRequests() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RideRequestDto rejectRideRequest(Integer rideRequestId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RideRequestDto approveRideRequest(Integer riderId) {
		// TODO Auto-generated method stub
		return null;
	}

}
