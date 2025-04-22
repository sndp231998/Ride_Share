package com.ride_share.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ride_share.controller.RideRequestWebSocketController;
import com.ride_share.entities.RideRequest;
import com.ride_share.entities.RiderApprovalRequest;
import com.ride_share.entities.User;
import com.ride_share.exceptions.ApiException;
import com.ride_share.exceptions.ResourceNotFoundException;
import com.ride_share.playoads.RideRequestDto;
import com.ride_share.playoads.RiderApprovalRequestDto;
import com.ride_share.playoads.UserDto;
import com.ride_share.repositories.CategoryRepo;
import com.ride_share.repositories.PricingRepo;
import com.ride_share.repositories.RideRequestRepo;
import com.ride_share.repositories.RiderApprovalRequestRepo;
import com.ride_share.repositories.UserRepo;
import com.ride_share.repositories.VehicleRepo;
import com.ride_share.service.MapService;
import com.ride_share.service.RiderApprovalRequestService;

@Service
public class RiderApprovalRequestServiceImpl implements RiderApprovalRequestService{

	 @Autowired
	    private ModelMapper modelMapper;

	    @Autowired
	    private RideRequestRepo rideRequestRepo;


	    @Autowired
	    private UserRepo userRepo;
	    
	    @Autowired
	    RiderApprovalRequestRepo riderApprovalRepo;


//	    
	    @Autowired
	    private RideRequestWebSocketController webSocketController;

	    @Override
	    public RiderApprovalRequestDto createRiderApproval(RiderApprovalRequestDto riderApprovalRequestDto,
	                                                      Integer rideRequestId, Integer userId) {

	        RideRequest ride = this.rideRequestRepo.findById(rideRequestId)
	                .orElseThrow(() -> new ResourceNotFoundException("RideRequest", "RideRequest ID", rideRequestId));

	        User user = this.userRepo.findById(userId)
	                .orElseThrow(() -> new ResourceNotFoundException("User", "User ID", userId));

	        if (user.getModes() != User.UserMode.RIDER) {
	            throw new ApiException("Must be in RIDER mode to approve a ride request.");
	        }

	        if (ride.getStatus() == RideRequest.RideStatus.PESSENGER_APPROVED ||
	            ride.getStatus() == RideRequest.RideStatus.REJECTED) {
	            throw new ApiException("This ride request has already been approved/rejected.");
	        }

	        double riderPrice = riderApprovalRequestDto.getProposed_price();
	        double passengerPrice = ride.getActualPrice();

	        if (riderPrice != 0.0 && riderPrice < passengerPrice) {
	            throw new ApiException("Rider Price cannot be less than the passenger's price.");
	        }

	        Optional<RiderApprovalRequest> existing = riderApprovalRepo.findByUserAndRideRequest(user, ride);

	        RiderApprovalRequest ab;

	        if (existing.isPresent()) {
	            // Update existing
	            ab = existing.get();
	            ab.setProposed_price(riderPrice != 0.0 ? riderPrice : passengerPrice);
	           // ab.setStatus(RiderApprovalRequest.ApprovedStatus.PENDING);
	            ab.setAddedDate(LocalDateTime.now());
	        } else {
	            // New entry
	            ab = new RiderApprovalRequest();
	            ab.setUser(user);
	            ab.setRideRequest(ride);
	            ab.setProposed_price(riderPrice != 0.0 ? riderPrice : passengerPrice);
	            ab.setStatus(RiderApprovalRequest.ApprovedStatus.PENDING);
	            ab.setAddedDate(LocalDateTime.now());
	        }

	        RiderApprovalRequest saved = this.riderApprovalRepo.save(ab);
	        return this.RiderApprovalToDto(saved);
	    }
	
	public RiderApprovalRequest dtoToRiderApproval(RiderApprovalRequestDto dto) {
	    RiderApprovalRequest entity = this.modelMapper.map(dto, RiderApprovalRequest.class);
	    if (dto.getUser() != null) {
	        entity.setUser(this.modelMapper.map(dto.getUser(), User.class));
	    }
	    if (dto.getRideRequest() != null) {
	        entity.setRideRequest(this.modelMapper.map(dto.getRideRequest(), RideRequest.class));
	    }
	    return entity;
	}
	public RiderApprovalRequestDto RiderApprovalToDto(RiderApprovalRequest entity) {
	    RiderApprovalRequestDto dto = new RiderApprovalRequestDto();
	    dto.setId(entity.getId());
	    dto.setProposed_price(entity.getProposed_price());
	    dto.setStatus(entity.getStatus());
	    dto.setAddedDate(entity.getAddedDate());

	    if (entity.getUser() != null) {
	        dto.setUser(this.modelMapper.map(entity.getUser(), UserDto.class));
	    }
	    if (entity.getRideRequest() != null) {
	        dto.setRideRequest(this.modelMapper.map(entity.getRideRequest(), RideRequestDto.class));
	    }
	    return dto;
	}


//	public RiderApprovalRequestDto RiderApprovalToDto(RiderApprovalRequest entity) {
//	    RiderApprovalRequestDto dto = this.modelMapper.map(entity, RiderApprovalRequestDto.class);
//	    if (entity.getUser() != null) {
//	        dto.setUser(this.modelMapper.map(entity.getUser(), UserDto.class));
//	    }
//	    if (entity.getRideRequest() != null) {
//	        dto.setRideRequest(this.modelMapper.map(entity.getRideRequest(), RideRequestDto.class));
//	    }
//	    return dto;
//	}



	
	
//	approval.setUser(user);//rider
//  	approval.setRideRequest(ride);//riderequestId 
//  	
//  	double pessengerprice=rideRequestDto.getActualPrice();
//  	 // Ensure rider's price is not less than passenger's price
//   //if (Riderprice != null && Riderprice != 0.0) {
//  	if ( Riderprice != 0.0) {
//  	 if  (Riderprice < pessengerprice) {
//  	        throw new ApiException("Rider Price cannot be less than the passenger's price.");
//  	    }else {
//  		approval.setProposed_price(Riderprice);
//  	    }
//  	}else {
//  		approval.setProposed_price(pessengerprice);
//  	}
//  
//  	approval.setStatus(RiderApprovalRequest.ApprovedStatus.PENDING);
//  	approval.setAddedDate(LocalDateTime.now());
// 
//  	// default false
//  	riderApprovalRepo.save(approval);
//
//   //yo comment save nagarda pn hune ho ..tara 
//    ride.setReplacePessengerPrice(Riderprice);
//    
//        
//  	  //ride.getReqriders().add(user);// main point
//  	    RideRequest approvedRide = this.rideRequestRepo.save(ride);
//  	    
//  	    
//  	    return this.modelMapper.map(approvedRide, RideRequestDto.class);
	
	
	
//    @Autowired
//    private VehicleRepo vehicleRepo;
//
//    @Autowired
//    private CategoryRepo categoryRepo;
//    
//    @Autowired
//    private MapService mapService;
//    
//    @Autowired
//    private PricingRepo pricingRepo;
//    
//    @Autowired
//    private RiderApprovalRequestRepo riderApprovalRequestRepo;
}
