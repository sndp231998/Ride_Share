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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.ride_share.controller.RideRequestWebSocketController;
import com.ride_share.entities.Category;
import com.ride_share.entities.RideCount;
import com.ride_share.entities.RideRequest;
import com.ride_share.entities.Rider;
import com.ride_share.entities.RiderApprovalRequest;
import com.ride_share.entities.User;
import com.ride_share.entities.Vehicle;
import com.ride_share.exceptions.ApiException;
import com.ride_share.exceptions.ResourceNotFoundException;
import com.ride_share.playoads.DistanceMatrixResponse;
import com.ride_share.playoads.Location;
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
import com.ride_share.repositories.RiderRepo;
import com.ride_share.repositories.UserRepo;
import com.ride_share.repositories.VehicleRepo;
import com.ride_share.service.MapService;
import com.ride_share.service.RiderApprovalRequestService;
import com.ride_share.service.RiderRatingService;

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

	    @Autowired
	    private RiderRepo riderRepo;
//	    
	    @Autowired
	    private RideRequestWebSocketController webSocketController;

	    @Autowired
	    private RiderRatingService riderRatingService;
	    
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
                   
	        RiderApprovalRequestDto dto = modelMapper.map(rejectedRide, RiderApprovalRequestDto.class);
	        dto.setRideRequestId(ride.getRideRequest().getRideRequestId()); // fix
	        // Send to dynamic topic
	        webSocketController.notifyPassengerRejectedRider(dto, riderAppId);

	        return dto;
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
		                user.getCurrentLocation().getLatitude(),//rider
		                user.getCurrentLocation().getLongitude(),//rider
		                ride.getS_latitude(),//passenger
		                ride.getS_longitude()
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
	        
	        RiderApprovalRequestDto dto = modelMapper.map(saved, RiderApprovalRequestDto.class);
	     // 3. Fetch updated list of riders
	        Set<RideRequestResponseDto> updatedRiders = getRidersForRideRequest(rideRequestId);
	    
	        webSocketController.notifyUpdatedRiderList(updatedRiders, rideRequestId);
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
		                dto.setRiderImage(user.getImageName());
		                dto.setUserId(user.getId());  // Set userId
		                dto.setMobileNo(user.getMobileNo());
		                Location loc = user.getCurrentLocation();
		                if (loc != null) {
		                    dto.setRiderLati(loc.getLatitude());
		                    dto.setRiderLong(loc.getLongitude());
		                }

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
		                
		                List<Rider>riders = riderRepo.findByUser(user);
		                if (riders != null && !riders.isEmpty()) {
		                    Rider rider = riders.get(0); // pick first rider for simplicity
		                    Double averageRating = riderRatingService.getAverageRatingByRiderId(rider.getId());
		                    dto.setAverageRating(averageRating);
		                } else {
		                    dto.setAverageRating(0.0);
		                }
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

	    @Scheduled(fixedRate = 2 * 60 * 1000) // हर 2 मिनेटमा run गर्छ
	    public void autoRejectPendingApprovals() {
	        LocalDateTime twoMinutesAgo = LocalDateTime.now().minusMinutes(2);

	        List<RiderApprovalRequest> pendingRequests = riderApprovalRepo
	            .findByStatusAndAddedDateBefore(RiderApprovalRequest.ApprovedStatus.PENDING, twoMinutesAgo);

	        for (RiderApprovalRequest request : pendingRequests) {
	            request.setStatus(RiderApprovalRequest.ApprovedStatus.REJECTED);
	        }

	        if (!pendingRequests.isEmpty()) {
	            riderApprovalRepo.saveAll(pendingRequests);
	            logger.info("Auto-rejected {} pending ride approvals older than 2 minutes", pendingRequests.size());

	            for (RiderApprovalRequest request : pendingRequests) {
	                RiderApprovalRequestDto dto = modelMapper.map(request, RiderApprovalRequestDto.class);
	                webSocketController.notifyPassengerRejectedRider(dto, request.getId()); // 👈 Send to dynamic topic
	            }
	        }
	    }


	
	
	
	
	


}
