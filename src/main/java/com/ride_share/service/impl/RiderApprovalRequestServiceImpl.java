package com.ride_share.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ride_share.controller.RideRequestWebSocketController;
import com.ride_share.entities.Category;
import com.ride_share.entities.RideCount;
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

	    
	    
	    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	    
	    
	    
	    @Override
	    public RiderApprovalRequestDto rejectRideApproval(Integer riderAppId) {
	        RiderApprovalRequest ride = riderApprovalRepo.findById(riderAppId)
	            .orElseThrow(() -> new ResourceNotFoundException("RiderApprovalRequest", "ID", riderAppId));

	        // ✅ If already rejected, then return same ride without changing anything
	        if (ride.getStatus() == RiderApprovalRequest.ApprovedStatus.REJECTED) {
	            return modelMapper.map(ride, RiderApprovalRequestDto.class);
	        }

	        // ✅ Set status to rejected
	        ride.setStatus(RiderApprovalRequest.ApprovedStatus.REJECTED);
	        
	        
	        
	        RiderApprovalRequest rejectedRide = riderApprovalRepo.save(ride);

	        // ✅ Notify WebSocket clients
	        try {
	            logger.info("📡 Sending WebSocket event for rejected rider approval ID: {}", riderAppId);
	            webSocketController.sendPessengerRejectedApp(ride);
	            logger.info("✅ WebSocket push successful for RiderApprovalRequest ID: {}", riderAppId);
	        } catch (Exception e) {
	            logger.error("❌ WebSocket push FAILED for RiderApprovalRequest ID: {}", riderAppId, e);
	        }
	      
	        return modelMapper.map(rejectedRide, RiderApprovalRequestDto.class);
	    }
	    
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
	       
	        Set<RideRequestResponseDto> updatedRiders = getRidersForRideRequest(rideRequestId);
	        webSocketController.sendRiderListForRideRequest(rideRequestId, updatedRiders);
	        return this.RiderApprovalToDto(saved);
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
		                dto.setId(req.getId());
		                dto.setRideRequestId(req.getRideRequest().getRideRequestId());
		                return dto;
		            })
		            .collect(Collectors.toSet());
		}



		public UserDto userToDto(User user) {
			UserDto userDto = this.modelMapper.map(user, UserDto.class);
			return userDto;
		}
		  
	    public RiderApprovalRequestDto RiderApprovalToDto(RiderApprovalRequest entity) {
	        RiderApprovalRequestDto dto = new RiderApprovalRequestDto();
	        
	        dto.setId(entity.getId());
	        dto.setProposed_price(entity.getProposed_price());
	        dto.setStatus(entity.getStatus());
	        dto.setAddedDate(entity.getAddedDate());
	        dto.setMinToReach(entity.getMinToReach());

	        if (entity.getUser() != null) {
	            dto.setUser(this.modelMapper.map(entity.getUser(), UserDto.class));
	        }
	        
	        if (entity.getRideRequest() != null) {
	            dto.setRideRequestId(entity.getRideRequest().getRideRequestId());
	        }
	        
	        return dto;
	    }

	    public RiderApprovalRequest dtoToRiderApproval(RiderApprovalRequestDto dto) {
	        RiderApprovalRequest entity = this.modelMapper.map(dto, RiderApprovalRequest.class);
	        
	        if (dto.getUser() != null) {
	            entity.setUser(this.modelMapper.map(dto.getUser(), User.class));
	        }
	        
	        if (dto.getRideRequestId() !=null) {
	            RideRequest ride = this.rideRequestRepo.findById(dto.getRideRequestId())
	                .orElseThrow(() -> new ResourceNotFoundException("RideRequest", "RideRequest ID", dto.getRideRequestId()));
	            entity.setRideRequest(ride);
	        }
	        
	        return entity;
	    }

	
//	@Override
//	public RideRequestDto approveRideRequestByPassenger(RiderApprovalRequestDto riderApprovalRequestDto, 
//			Integer Id, Integer rideRequestId) {
//		
//		 RiderApprovalRequest riderApproval = riderApprovalRepo.findById(Id)
//			        .orElseThrow(() -> new ResourceNotFoundException("RideApprovalRequest", "Id", Id));
//
//	    // Fetch the ride request using the rideRequestId
//	    RideRequest rideRequest = rideRequestRepo.findById(rideRequestId)
//	        .orElseThrow(() -> new ResourceNotFoundException("RideRequest", "RideRequest ID", rideRequestId));
//	    if (rideRequest.getStatus() == RideRequest.RideStatus.PESSENGER_APPROVED) {
//	        throw new ApiException("Already approved");
//	    }
//
//	    rideRequest.setStatus(RideRequest.RideStatus.PESSENGER_APPROVED);
//	    // ✅ Ensures that the selected rider is actually in the list of users who requested this ride
//	    riderApproval.setStatus(RiderApprovalRequest.ApprovedStatus.PESSENGER_APPROVED);
//	    rideRequest.setActualPrice(riderApproval.getProposed_price());
//	    // ✅ Updates the ride request with the approved rider's ID and changes status to PESSENGER_APPROVED
//	    rideRequest.setRidebookedId(riderApproval.getUser().getId());
//	    // Save the updated ride request and return it as a DTO
//	    RideRequest ridereq = rideRequestRepo.save(rideRequest);
//	    RiderApprovalRequest approvedreq= riderApprovalRepo.save(riderApproval);
//	// Notify WebSocket clients
//	  webSocketController.sendRideStatusUpdate(rideRequest);
//	  // ✅ Return DTO
//	  // ✅ ModelMapper बाट map गरेर return
//	    return modelMapper.map(ridereq, RideRequestDto.class);
//	}
//	
	
	
	
	
	


}
