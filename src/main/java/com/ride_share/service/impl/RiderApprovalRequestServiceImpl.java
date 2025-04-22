package com.ride_share.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ride_share.controller.RideRequestWebSocketController;
import com.ride_share.entities.RideRequest;
import com.ride_share.entities.RiderApprovalRequest;
import com.ride_share.entities.User;
import com.ride_share.entities.Vehicle;
import com.ride_share.exceptions.ApiException;
import com.ride_share.exceptions.ResourceNotFoundException;
import com.ride_share.playoads.DistanceMatrixResponse;
import com.ride_share.playoads.RideRequestDto;
import com.ride_share.playoads.RideRequestResponseDto;
import com.ride_share.playoads.RiderApprovalRequestDto;
import com.ride_share.playoads.RiderDto;
import com.ride_share.playoads.UserDto;
import com.ride_share.playoads.VehicleDto;
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
      private VehicleRepo vehicleRepo;
	    
	    @Autowired
	    MapServiceImpl map;
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
	           
	        	  
		        double min = 0.0;
		       
		        
		        try {
		            DistanceMatrixResponse response = map.getDistanceMatrixData(
		                user.getCurrentLocation().getLatitude(),
		                user.getCurrentLocation().getLongitude(),
		                ride.getD_latitude(),
		                ride.getD_longitude()
		            );
		            min=response.getDurationMin();
		        } catch (Exception e) {
		            throw new ApiException("Failed to calculate distance: " + e.getMessage());
		        }
		           
		          
	        	
	            ab = new RiderApprovalRequest();
	            ab.setUser(user);
	            ab.setMinToReach(min);
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
	


	@Override
	public Set<RideRequestResponseDto> getRidersForRideRequest(Integer rideRequestId) {
	    List<RiderApprovalRequest> pendingApprovals = riderApprovalRepo.findByRideRequest_RideRequestIdAndStatus(
	            rideRequestId, RiderApprovalRequest.ApprovedStatus.PENDING
	    );
	    
	    return pendingApprovals.stream()
	            .map(req -> {
	                User user = req.getUser();
	                RideRequestResponseDto dto = new RideRequestResponseDto();
	                dto.setName(user.getName());
	                dto.setUserId(user.getId());  // Set userId
	                dto.setMobileNo(user.getMobileNo());
	                List<Vehicle> vehicles = vehicleRepo.findByUser(user);
	                if (vehicles != null && !vehicles.isEmpty()) {
	                    // For example, pick the first vehicle's attributes
	                    Vehicle vehicle = vehicles.get(0);  // Assuming you want the first vehicle for each user

	                    // Add additional vehicle attributes
	                    dto.setVehicleBrand(vehicle.getVehicleBrand());
	                  dto.setVehicleNumber(vehicle.getVehicleNumber());
	                  
	                   // dto.setVehicleModel(vehicle.getVehicleModel());
	                    dto.setVehicleType(vehicle.getVehicleType());
	                    // You can also add more attributes as needed
	                } else {
	                    dto.setVehicleBrand("No vehicle");
	                    dto.setVehicleType("No Vehicle..");
	                }
	                //dto.setProposedPrice(req.getProposed_price());
	                dto.setProposedPrice(req.getProposed_price());
	                dto.setMinToReach(req.getMinToReach());
	                return dto;
	            })
	            .collect(Collectors.toSet());
	}



	public UserDto userToDto(User user) {
		UserDto userDto = this.modelMapper.map(user, UserDto.class);
		return userDto;
	}
	


}
