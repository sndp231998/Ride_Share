package com.ride_share.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
		 RideRequest ride = this.rideRequestRepo.findById(rideRequestId)
	                .orElseThrow(() -> new ResourceNotFoundException("RideRequest ", "rideRequest id", rideRequestId));

	        this.rideRequestRepo.delete(ride);
		
	}

	@Override
	public RideRequestDto getRideRequestById(Integer rideRequestId) {
		RideRequest ride = this.rideRequestRepo.findById(rideRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("RideRequest", "rideRequest id", rideRequestId));
        return this.modelMapper.map(ride, RideRequestDto.class);
	}

	@Override
	public List<RideRequestDto> getRideRequestByUser(Integer userId) {
		 User user = this.userRepo.findById(userId)
	                .orElseThrow(() -> new ResourceNotFoundException("User ", "userId ", userId));
	        List<RideRequest> rides = this.rideRequestRepo.findByUser(user);

	        List<RideRequestDto> rideDtos = rides.stream().map((ride) -> this.modelMapper.map(ride, RideRequestDto.class))
	                .collect(Collectors.toList());

	        return rideDtos;
	}

	@Override
	public List<RideRequestDto> getAllRideRequests() {
	    List<RideRequest> ride = this.rideRequestRepo.findAll();
	    List<RideRequestDto> riderDtos = ride.stream().map(this::rideRequestToDto).collect(Collectors.toList());

	    return riderDtos;
	}

	public RideRequest dtoToRideRequest(RideRequestDto rideRequestDto) {
	    RideRequest ride = this.modelMapper.map(rideRequestDto, RideRequest.class);
	    return ride;
	}

	public RideRequestDto rideRequestToDto(RideRequest rideRequest) {
	    RideRequestDto rideRequestDto = this.modelMapper.map(rideRequest, RideRequestDto.class);
	    return rideRequestDto;
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
