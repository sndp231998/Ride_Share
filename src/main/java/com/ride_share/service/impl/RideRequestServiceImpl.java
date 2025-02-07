package com.ride_share.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ride_share.entities.RideRequest;

import com.ride_share.entities.User;
import com.ride_share.exceptions.ResourceNotFoundException;
import com.ride_share.playoads.RideRequestDto;

import com.ride_share.repositories.RideRequestRepo;

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

	    // Ensure user is in PASSENGER mode
	    if (user.getModes() != User.UserMode.PESSENGER) {
	        throw new IllegalStateException("User must be in PESSENGER mode to create a ride request.");
	    }

	    RideRequest ride = this.modelMapper.map(rideRequestDto, RideRequest.class);
	    ride.setAddedDate(LocalDateTime.now());
	    ride.setActualPrice(rideRequestDto.getActualPrice());
	    ride.setSource(rideRequestDto.getSource());
	    ride.setDestination(rideRequestDto.getDestination());
	    ride.setStatus(RideRequest.RideStatus.PENDING);
	   ride.setUser(user); 
	   // Linking the ride request to the user
            ;
	    RideRequest savedRideReq = this.rideRequestRepo.save(ride);
	    return this.modelMapper.map(savedRideReq, RideRequestDto.class);
	}


	@Override
	public RideRequestDto updateRideRequest(RideRequestDto rideRequestDto, Integer rideRequestId) {
		RideRequest req = this.rideRequestRepo.findById(rideRequestId)
				.orElseThrow(()->new ResourceNotFoundException("RideRequest","RideRequest ID",rideRequestId));
		req.setDestination(rideRequestDto.getDestination());
		req.setSource(rideRequestDto.getSource());
		req.setActualPrice(rideRequestDto.getActualPrice());
		RideRequest updatedRide=this.rideRequestRepo.save(req);
		return this.modelMapper.map(updatedRide, RideRequestDto.class);
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
		 RideRequest ride = this.rideRequestRepo.findById(rideRequestId)
	                .orElseThrow(() -> new ResourceNotFoundException("RideRequest", "RideRequest ID", rideRequestId));

	        // Only allow rejection if status is PENDING
//	        if (rider.getStatus() != Rider.RiderStatus.PENDING) {
//	            throw new IllegalStateException("Cannot reject rider. The application is not in PENDING status.");
//	        }
           ride.setStatus(RideRequest.RideStatus.REJECTED);
	        
	       // ride.setUpdatedDate(LocalDateTime.now());

	        RideRequest rejectedRide = this.rideRequestRepo.save(ride);
	        return this.modelMapper.map(rejectedRide, RideRequestDto.class);
	}

	//approved by Rider
	@Override
	public RideRequestDto approveRideRequestByRider(RideRequestDto rideRequestDto,Integer userId, Integer rideRequestId) {
	    RideRequest ride = this.rideRequestRepo.findById(rideRequestId)
	            .orElseThrow(() -> new ResourceNotFoundException("RideRequest", "RideRequest ID", rideRequestId));

	    User user = this.userRepo.findById(userId)
	            .orElseThrow(() -> new ResourceNotFoundException("User", "User ID", userId));

	    if (user.getModes() != User.UserMode.RIDER) {  
	        throw new IllegalStateException("User must be in RIDER mode to approved a ride request.");
	    }
	    // Check if the ride has already been approved by another rider [arko rider le approved gari sakyako raixa vane
	    if (ride.getStatus() == RideRequest.RideStatus.RIDER_APPROVED) {
	        throw new IllegalStateException("This ride request has already been approved by another rider.");
	    }

	    // Allow rider to update price during approval
	   // ride.setActualPrice(rideRequestDto.getActualPrice());
	    ride.setStatus(RideRequest.RideStatus.RIDER_APPROVED);
	 // Update actual price only if provided
	    if (rideRequestDto.getActualPrice() != null && !rideRequestDto.getActualPrice().isEmpty()) {
	        ride.setActualPrice(rideRequestDto.getActualPrice());
	    }
	    
	    RideRequest approvedRide = this.rideRequestRepo.save(ride);
	    return this.modelMapper.map(approvedRide, RideRequestDto.class);
	}
	
	@Override
	public RideRequestDto approveRideRequestByPassenger(Integer rideRequestId) {
	    RideRequest ride = this.rideRequestRepo.findById(rideRequestId)
	            .orElseThrow(() -> new ResourceNotFoundException("RideRequest", "RideRequest ID", rideRequestId));

	    // Passenger can approve only if the ride is already approved by a rider
	    if (ride.getStatus() != RideRequest.RideStatus.RIDER_APPROVED) {
	        throw new IllegalStateException("The ride request must be approved by a rider before passenger approval.");
	    }

	    ride.setStatus(RideRequest.RideStatus.PESSENGER_PAPPROVED);

	    RideRequest approvedRide = this.rideRequestRepo.save(ride);
	    return this.modelMapper.map(approvedRide, RideRequestDto.class);
	}

}
