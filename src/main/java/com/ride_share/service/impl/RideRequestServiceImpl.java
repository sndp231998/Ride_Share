package com.ride_share.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ride_share.controller.RideRequestWebSocketController;
import com.ride_share.entities.Category;
import com.ride_share.entities.Pricing;
import com.ride_share.entities.RideCount;
import com.ride_share.entities.RideRequest;
import com.ride_share.entities.Rider;
import com.ride_share.entities.RiderApprovalRequest;
import com.ride_share.entities.RiderApprovalRequest.ApprovedStatus;
import com.ride_share.entities.User;
import com.ride_share.entities.User.UserMode;
import com.ride_share.entities.Vehicle;
import com.ride_share.exceptions.ApiException;
import com.ride_share.exceptions.ResourceNotFoundException;
import com.ride_share.playoads.DistanceMatrixResponse;
import com.ride_share.playoads.Location;
import com.ride_share.playoads.NotificationDto;
import com.ride_share.playoads.PriceInfoDto;
import com.ride_share.playoads.RideInfoDto;
import com.ride_share.playoads.RideRequestDto;
import com.ride_share.playoads.RideRequestResponseDto;
import com.ride_share.playoads.RideRequestWithDistanceDto;
import com.ride_share.playoads.RiderApprovalRequestDto;
import com.ride_share.playoads.UserDto;
import com.ride_share.playoads.VehicleDto;
import com.ride_share.repositories.CategoryRepo;
import com.ride_share.repositories.PricingRepo;
import com.ride_share.repositories.RideCountRepo;
import com.ride_share.repositories.RideRequestRepo;
import com.ride_share.repositories.RiderApprovalRequestRepo;
import com.ride_share.repositories.RiderRepo;
import com.ride_share.repositories.UserRepo;
import com.ride_share.repositories.VehicleRepo;
import com.ride_share.service.MapService;
import com.ride_share.service.NotificationService;
import com.ride_share.service.RideRequestService;

@Service
public class RideRequestServiceImpl implements RideRequestService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private RideRequestRepo rideRequestRepo;
    
    @Autowired
   private MapServiceImpl mapServiceImpl;

    @Autowired
    private UserRepo userRepo;
    
    @Autowired
    RiderApprovalRequestRepo riderApprovalRepo;

    @Autowired
    private VehicleRepo vehicleRepo;
    @Autowired
    private RiderRepo riderRepo;

    @Autowired
    private CategoryRepo categoryRepo;
    
    @Autowired
    private MapService mapService;
    
    @Autowired
    private PricingRepo pricingRepo;
    @Autowired
    NotificationService notificationService;
    @Autowired
    RideCountRepo rideCountRepo;   
    @Autowired
    private RideRequestWebSocketController webSocketController;

    
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    // Existing methods (create, update, delete, get, etc.)
    
    
    @Override
    public RideRequestDto rideComplete(Integer rideRequestId) {
        RideRequest ride = rideRequestRepo.findById(rideRequestId)
            .orElseThrow(() -> new ResourceNotFoundException("RideRequest", "RideRequest ID", rideRequestId));

        ride.setStatus(RideRequest.RideStatus.RIDE_COMPLETE);
        rideRequestRepo.save(ride);

        RideRequestDto dto = modelMapper.map(ride, RideRequestDto.class);
        webSocketController.rideComplete(dto, rideRequestId);
        return dto;
    }

    @Override
    public RideRequestDto ridePickup(Integer rideRequestId) {
        RideRequest ride = rideRequestRepo.findById(rideRequestId)
            .orElseThrow(() -> new ResourceNotFoundException("RideRequest", "RideRequest ID", rideRequestId));

        ride.setStatus(RideRequest.RideStatus.PICKUP);
        rideRequestRepo.save(ride);

        RideRequestDto dto = modelMapper.map(ride, RideRequestDto.class);
        webSocketController.ridePickup(dto, rideRequestId);
        return dto;
    }

    public RideRequestDto convertToDto(RideRequest rideRequest) {
        return modelMapper.map(rideRequest, RideRequestDto.class);
    }
    @Override
    public List<RideRequestDto> getRequestsByUserId(Integer userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ApiException("User not found"));

        List<RideRequest> requests = rideRequestRepo.findByUser(user);

        return requests.stream()
                .map(rideRequest -> modelMapper.map(rideRequest, RideRequestDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<RideRequestDto> getRequestsByRiderId(Integer userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ApiException("User not found"));

        List<RideRequest> requests = rideRequestRepo.findByRidebookedId(user.getId()); // Pass userId here

        return requests.stream()
                .map(rideRequest -> modelMapper.map(rideRequest, RideRequestDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public RideRequestDto rejectRideRequest(Integer rideRequestId) {
        RideRequest ride = rideRequestRepo.findById(rideRequestId)
            .orElseThrow(() -> new ResourceNotFoundException("RideRequest", "RideRequest ID", rideRequestId));

        // ✅ If already rejected, then return same ride without changing anything
        if (ride.getStatus() == RideRequest.RideStatus.REJECTED) {
            return modelMapper.map(ride, RideRequestDto.class);
        }

        // ✅ Set status to rejected
        ride.setStatus(RideRequest.RideStatus.REJECTED);
        RideRequest rejectedRide = rideRequestRepo.save(ride);

        // ✅ ride.getRidebookedId() null xa vane skip
        if (ride.getRidebookedId() != null) {
            LocalDate today = LocalDate.now();
            User user = userRepo.findById(ride.getRidebookedId())
                    .orElseThrow(() -> new ApiException("User not found"));
            Category category = ride.getCategory();
            RideCount existing = rideCountRepo.findByUserAndCategoryAndDate(user, category, today.atStartOfDay());

            if (existing != null) {
                existing.setTotalRide(existing.getTotalRide() - 1);
                rideCountRepo.save(existing);
            }
            NotificationDto notificationDto = new NotificationDto();
            notificationDto.setMessage("passenger has been rejected ride request.");
            int userId=user.getId();
            notificationService.createNotification(notificationDto, userId);
        }
        
        RideRequestDto dto = modelMapper.map(rejectedRide, RideRequestDto.class);

        // ✅ Send to dynamic topic
        webSocketController.sendRideRejected(dto, rideRequestId);

        return dto;
    }

    @Override //id=rideapproval
    public RideRequestDto approveRideRequestByPassenger(Integer Id, Integer rideRequestId) {
        
        RiderApprovalRequest riderApproval = riderApprovalRepo.findById(Id)
                .orElseThrow(() -> new ResourceNotFoundException("RideApprovalRequest", "Id", Id));

        RideRequest rideRequest = rideRequestRepo.findById(rideRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("RideRequest", "RideRequest ID", rideRequestId));

        if (rideRequest.getStatus() == RideRequest.RideStatus.PESSENGER_APPROVED) {
            throw new ApiException("Already approved");
        }

        rideRequest.setStatus(RideRequest.RideStatus.PESSENGER_APPROVED);
        riderApproval.setStatus(RiderApprovalRequest.ApprovedStatus.PESSENGER_APPROVED);
        rideRequest.setReplacePessengerPrice(riderApproval.getProposed_price());
        //rideRequest.setActualPrice(riderApproval.getProposed_price());
        rideRequest.setRidebookedId(riderApproval.getUser().getId());

        // Get user, category, and today's date
        User user = riderApproval.getUser();
        Category category = rideRequest.getCategory();
        LocalDate today = LocalDate.now(); // only date, no time
     // Check if record exists for this user, category, and today
        RideCount existing = rideCountRepo.findByUserAndCategoryAndDate(user, category, today.atStartOfDay());

        if (existing != null) {
            existing.setTotalRide(existing.getTotalRide() + 1);
            rideCountRepo.save(existing);
        } else {
            RideCount rideCount = new RideCount();
            rideCount.setUser(user);
            rideCount.setCategory(category);
            rideCount.setTotalRide(1);
            rideCount.setDate(today.atStartOfDay()); // store only date part
            rideCountRepo.save(rideCount);
           
        }
   
        // Save
       rideRequestRepo.save(rideRequest);
        riderApprovalRepo.save(riderApproval);

     // ✅ Send socket to dynamic topic
        RideRequestDto dto = modelMapper.map(rideRequest, RideRequestDto.class);
        webSocketController.sendPassengerApproved(dto, rideRequestId); // 👈 dynamic topic

        return dto;
    }


    // Method to approve a RideRequest by a rider
  //approved by Rider
    //userId== rider ko user id halnu paro
  	@Override
  	public RideRequestDto approveRideRequestByRider(RideRequestDto rideRequestDto,Integer userId, Integer rideRequestId) {
  	    RideRequest ride = this.rideRequestRepo.findById(rideRequestId)
  	            .orElseThrow(() -> new ResourceNotFoundException("RideRequest", "RideRequest ID", rideRequestId));

  	    User user = this.userRepo.findById(userId)
  	            .orElseThrow(() -> new ResourceNotFoundException("User", "User ID", userId));
  	
  	    if (user.getModes() != User.UserMode.RIDER) {  
  	        throw new ApiException("Must be in RIDER mode to approved a ride request.");
  	    }
  	    // Check if the ride has already been approved by another rider [arko rider le approved gari sakyako raixa vane
  	    if (ride.getStatus() == RideRequest.RideStatus.PESSENGER_APPROVED ||
  	    		ride.getStatus() == RideRequest.RideStatus.REJECTED
  	    		) {
  	        throw new ApiException("This ride request has already been approved/rejected.");
  	    }
  	   
  	 RiderApprovalRequest approval = new RiderApprovalRequest();
  	approval.setUser(user);//rider
  	approval.setRideRequest(ride);//riderequestId 
  	double Riderprice=rideRequestDto.getReplacePessengerPrice();
  	double pessengerprice=rideRequestDto.getActualPrice();
  	 // Ensure rider's price is not less than passenger's price
   //if (Riderprice != null && Riderprice != 0.0) {
  	if ( Riderprice != 0.0) {
  	 if  (Riderprice < pessengerprice) {
  	        throw new ApiException("Rider Price cannot be less than the passenger's price.");
  	    }else {
  		approval.setProposed_price(Riderprice);
  	    }
  	}else {
  		approval.setProposed_price(pessengerprice);
  	}
  
  	approval.setStatus(RiderApprovalRequest.ApprovedStatus.PENDING);
  	approval.setAddedDate(LocalDateTime.now());
 
  	// default false
  	riderApprovalRepo.save(approval);

   //yo comment save nagarda pn hune ho ..tara 
    ride.setReplacePessengerPrice(Riderprice);
    
 
  	    RideRequest approvedRide = this.rideRequestRepo.save(ride);

        // Notify clients via websocket (optional)
  	 // webSocketController.sendRiderApproved(modelMapper.map(approvedRide, RideRequestDto.class));
  
  	    return this.modelMapper.map(approvedRide, RideRequestDto.class);
  	}
    
    //----kam xina yo method ko delete garda hunxa
    @Override
    public List<RideRequestDto> getRideRequestsByUserCategory(int userId) {
    
    	 Optional<Vehicle> vehicleOpt = vehicleRepo.findByUserId(userId);
    	 Integer categoryId = null;
    	 if (vehicleOpt.isPresent()) {
             categoryId = vehicleOpt.get().getCategory().getCategoryId();
         } else {
             // Step 2: If no vehicle, find from Rider
             Optional<Rider> riderOpt = riderRepo.findByUserId(userId);
             if (riderOpt.isPresent()) {
                 categoryId = riderOpt.get().getCategory().getCategoryId();
             }
         }
    	 if (categoryId == null) {
             throw new ApiException("User does not have a category assigned via Vehicle or Rider");
         }
    	 
    	 // Step 3: Fetch ride requests by category
         List<RideRequest> rideRequests = rideRequestRepo.findByCategory_CategoryId(categoryId);
        		
      // Step 4: Map entity to DTO
         List<RideRequestDto> dtoList = rideRequests.stream().map(ride -> 
         RideRequestDto.builder()
         .rideRequestId(ride.getRideRequestId())
         .actualPrice(ride.getActualPrice())
         .d_latitude(ride.getD_latitude())
         .d_longitude(ride.getD_longitude())
         .d_Name(ride.getD_Name())
         .s_latitude(ride.getS_latitude())
         .s_longitude(ride.getS_longitude())
         .s_Name(ride.getS_Name())
                 
         .build()
        		 ).collect(Collectors.toList());

         return dtoList;
     }
 
    
    
    
    @Override
    public RideRequestDto createRideRequest(RideRequestDto rideRequestDto, Integer userId,Integer categoryId) {
        // Fetch user details
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "User ID", userId));

        Category category = this.categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "category id ", categoryId));

        
        // Ensure user is in PASSENGER mode
        if (user.getModes() != User.UserMode.PESSENGER) {
            throw new ApiException("User must be in PESSENGER mode to create a ride request.");
        }
     // Check if the user has a current location
       // Location currentLocation = user.getCurrentLocation();
        if (user.getCurrentLocation() == null) {
            throw new ApiException("User's current location is not set.");
        }
        LocalDateTime locationTime=user.getCurrentLocation().getTimestamp();
        LocalDateTime currentTime = LocalDateTime.now();

        // Check if the location is stale
        if (locationTime == null || locationTime.isBefore(currentTime.minusMinutes(59))) {
            throw new ApiException(" Please update your current location.");
        }

        double distancekm = 0.0;
        String destinationAdd;
        String sourceAdd;
        Double durationMin;
        
        try {
            DistanceMatrixResponse response = mapService.getDistanceMatrixData(
                user.getCurrentLocation().getLatitude(),
                user.getCurrentLocation().getLongitude(),
                rideRequestDto.getD_latitude(),
                rideRequestDto.getD_longitude()
            );

            distancekm = response.getDistanceKm();
           sourceAdd= response.getOriginAddress();
            destinationAdd=response.getDestinationAddress();
            durationMin=response.getDurationMin();
            		
        } catch (Exception e) {
            throw new ApiException("Failed to calculate distance: " + e.getMessage());
        }
       
   

        // Determine the state (province) based on user's current location
        String province;
        try {
            province = mapServiceImpl.getState(
                    user.getCurrentLocation().getLatitude(),
                    user.getCurrentLocation().getLongitude()
            );
        } catch (Exception e) {
            throw new ApiException("Error determining city.");
        }

       // logger.info("Fetching pricing data for province: {} and category ID: {}", province, categoryId);
        // Fetch pricing data based on province and category
        Pricing pricing = pricingRepo.findByProvinceAndCategory_CategoryId(province, categoryId)
                .orElseThrow(() -> new ApiException("Pricing not available for the given province and category."));

        //logger.info("Pricing data fetched successfully: {}", pricing);
        double perkm=pricing.getPerKmRate();
        double basefare=pricing.getBaseFare();
        
        double generatedPrice = basefare + (perkm * distancekm);
        	
        	//user bata pani price line
        	double givenPrice=0.0;
        		//usser bata user ko pn price same attribute me line
        	givenPrice= rideRequestDto.getActualPrice();
        	if (givenPrice == 0.0) {
        		//do nothing
        	    // If user did not give price, set actualPrice = generatedPrice
        	    //rideRequestDto.setActualPrice(generatedPrice);
        		rideRequestDto.setActualPrice(generatedPrice); 
        	}else if (givenPrice < generatedPrice || givenPrice > generatedPrice + 50) {
                throw new ApiException("Recommend price:"+generatedPrice);
            } else {
                rideRequestDto.setActualPrice(givenPrice); // use user-given price
            }
             // Create a new RideRequest
         RideRequest rideRequest = new RideRequest();

         logger.debug("Setting Destination Coordinates: Latitude={}, Longitude={}", 
                 rideRequestDto.getD_latitude(), 
                 rideRequestDto.getD_longitude());

         if (rideRequestDto.getD_Name() != null) {
        	    rideRequest.setD_Name(rideRequestDto.getD_Name());
        	} else {
        	    rideRequest.setD_Name(destinationAdd);
        	}

      // destination --saved
         rideRequest.setD_latitude(rideRequestDto.getD_latitude());
         rideRequest.setD_longitude(rideRequestDto.getD_longitude());
        rideRequest.setActualPrice(givenPrice);
    rideRequest.setTotal_Km(distancekm);
    //rideRequest.setD_Name(destinationAdd);
    rideRequest.setS_Name(sourceAdd);
    rideRequest.setTotal_min(durationMin.intValue()); // or (int)(double)durationMin
    

        //------------yo source-saved----------------------
       
        rideRequest.setS_latitude(user.getCurrentLocation().getLatitude());
        rideRequest.setS_longitude(user.getCurrentLocation().getLongitude());
        //source.setTimestamp(LocalDateTime.now());
        
       rideRequest.setAddedDate(LocalDateTime.now());
        rideRequest.setStatus(RideRequest.RideStatus.PENDING);
        rideRequest.setUser(user); // Link the ride request to the user
        rideRequest.setCategory(category);
        // Save the ride request
        RideRequest newRiderequest = rideRequestRepo.save(rideRequest);
     // ✅ Step 1: Get eligible rider userIds
        List<Integer> eligibleRiderUserIds = getEligibleRidersForRequest(newRiderequest);
        logger.info("✅ Eligible rider userIds: {}", eligibleRiderUserIds);

        // ✅ Step 2: Convert RideRequest entity to DTO
        rideRequestDto = modelMapper.map(newRiderequest, RideRequestDto.class);
        logger.info("✅ RideRequest DTO created: {}", rideRequestDto);

        // ✅ Step 3: Send eligible riders to socket
        webSocketController.sendEligibleRiders(eligibleRiderUserIds, rideRequestDto);
        logger.info("✅ Sent eligible riders via WebSocket");

        // ✅ Step 4: Return DTO
        return rideRequestDto;
    }
    
    public List<Integer> getEligibleRidersForRequest(RideRequest request) {
    	double reqLat = request.getS_latitude();
        double reqLng = request.getS_longitude();
        int requestCategoryId = request.getCategory().getCategoryId();
        
        List<Rider> approvedRiders = riderRepo.findByStatus(Rider.RiderStatus.APPROVED);
        List<Integer> riderIds = approvedRiders.stream()
                .map(Rider::getId)
                .collect(Collectors.toList());
        logger.info("✅ Approved Riders fetched: {}", riderIds);
        List<Integer> eligibleRiderIds = new ArrayList<>();

        for (Rider rider : approvedRiders) {
            User riderUser = rider.getUser();

            // 2. Location null भए skip गर्ने
            if (riderUser == null || riderUser.getCurrentLocation().getLatitude() == null) {
            	
            logger.debug("❌ Rider ID {} skipped: No user or location.", rider.getId());
            continue;
            }
            Location riderLocation = riderUser.getCurrentLocation();
            logger.debug("🔍 Checking rider ID {} at location: {}, {}", 
                    riderUser.getId(), riderLocation.getLatitude(), riderLocation.getLongitude());

            // 3. Distance check गर्ने
            try {
                DistanceMatrixResponse distanceData = mapServiceImpl.getDistanceMatrixData(
                        reqLat, reqLng,
                        riderLocation.getLatitude(),
                        riderLocation.getLongitude()
                );
                logger.debug("📏 Distance to rider {} is {} km", riderUser.getId(), distanceData.getDistanceKm());
                // 4. Category match + distance <= 10km
                if (distanceData.getDistanceKm() <= 10 &&
                    rider.getCategory() != null &&
                    rider.getCategory().getCategoryId() == requestCategoryId) {
                	
                	 logger.debug("✅ Rider ID {} is eligible", riderUser.getId());
                    eligibleRiderIds.add(riderUser.getId());
                    
                } else {
                	logger.debug("❌ Rider ID {} is NOT eligible (distance: {} km, category match: {})",
                	        riderUser.getId(),
                	        distanceData.getDistanceKm(),
                	        (rider.getCategory() != null && rider.getCategory().getCategoryId() == requestCategoryId)
                	    );
                }
            } catch (Exception e) {
                System.err.println("Distance check failed for Rider ID " + rider.getId());
            }
        }

        return eligibleRiderIds;
    }
        
    @Override
    public RideRequestDto updateRideRequest(RideRequestDto rideRequestDto, Integer rideRequestId) {
        RideRequest rideRequest = rideRequestRepo.findById(rideRequestId)
            .orElseThrow(() -> new ResourceNotFoundException("RideRequest", "RideRequest ID", rideRequestId));

        // Only allow updates if the ride status is PENDING
        if (rideRequest.getStatus() != RideRequest.RideStatus.PENDING) {
            throw new ApiException("Ride request can only be updated when the status is PENDING.");
        }

        User user = rideRequest.getUser();

        // Ensure user has current location
        if (user.getCurrentLocation() == null) {
            throw new ApiException("User's current location is not set.");
        }

        LocalDateTime locationTime = user.getCurrentLocation().getTimestamp();
        LocalDateTime currentTime = LocalDateTime.now();

        if (locationTime == null || locationTime.isBefore(currentTime.minusMinutes(59))) {
            throw new ApiException("Please update your current location.");
        }

        double distancekm;
        String destinationAdd;
        String sourceAdd;
        Double durationMin;

        try {
            DistanceMatrixResponse response = mapService.getDistanceMatrixData(
                user.getCurrentLocation().getLatitude(),
                user.getCurrentLocation().getLongitude(),
                rideRequestDto.getD_latitude(),
                rideRequestDto.getD_longitude()
            );

            distancekm = response.getDistanceKm();
            sourceAdd = response.getOriginAddress();
            destinationAdd = response.getDestinationAddress();
               durationMin=response.getDurationMin();
        } catch (Exception e) {
            throw new ApiException("Failed to calculate distance: " + e.getMessage());
        }

        String province;
        try {
            province = mapServiceImpl.getState(
                user.getCurrentLocation().getLatitude(),
                user.getCurrentLocation().getLongitude()
            );
        } catch (Exception e) {
            throw new ApiException("Error determining city.");
        }

        Pricing pricing = pricingRepo.findByProvinceAndCategory_CategoryId(province, rideRequest.getCategory().getCategoryId())
            .orElseThrow(() -> new ApiException("Pricing not available for the given province and category."));

        double generatedPrice = pricing.getBaseFare() + (pricing.getPerKmRate() * distancekm);
        double givenPrice = rideRequestDto.getActualPrice();

        if (givenPrice == 0.0) {
            rideRequest.setActualPrice(generatedPrice);
        } else if (givenPrice < generatedPrice || givenPrice > generatedPrice + 50) {
            throw new ApiException("Recommended price: " + generatedPrice);
        } else {
            rideRequest.setActualPrice(givenPrice);
        }

        // Update location and address details
        rideRequest.setS_latitude(user.getCurrentLocation().getLatitude());
        rideRequest.setS_longitude(user.getCurrentLocation().getLongitude());
        rideRequest.setD_latitude(rideRequestDto.getD_latitude());
        rideRequest.setD_longitude(rideRequestDto.getD_longitude());
        rideRequest.setS_Name(sourceAdd);
        if (rideRequestDto.getD_Name() != null) {
    	    rideRequest.setD_Name(rideRequestDto.getD_Name());
    	} else {
    	    rideRequest.setD_Name(destinationAdd);
    	}
        rideRequest.setTotal_Km(distancekm);
        rideRequest.setTotal_min(durationMin.intValue());
        rideRequest.setStatus(RideRequest.RideStatus.PENDING); // still pending

        RideRequest updatedRide = rideRequestRepo.save(rideRequest);
        List<Integer> eligibleRiderUserIds = getEligibleRidersForRequest(updatedRide);
        logger.info("✅ Eligible rider userIds: {}", eligibleRiderUserIds);
        
        rideRequestDto = modelMapper.map(updatedRide, RideRequestDto.class);
        logger.info("✅ RideRequest DTO created: {}", rideRequestDto);
        webSocketController.sendEligibleRiders(eligibleRiderUserIds, rideRequestDto);
        logger.info("✅ Sent eligible riders via WebSocket");
        return  rideRequestDto;
    }


    
    @Override
    public List<RideRequestDto> getSortedPendingRideRequests(int riderUserId) {

        // 1. Get Rider's current location
        User rider = userRepo.findById(riderUserId)
                .orElseThrow(() -> new ApiException("User not found"));
        
        Location riderLocation = rider.getCurrentLocation();
        if (riderLocation == null) {
            throw new ApiException("User current location not available");
        }

        // 2. Get Rider's category
        Optional<Vehicle> vehicleOpt = vehicleRepo.findByUserId(riderUserId);
        Integer categoryId = null;
        
        if (vehicleOpt.isPresent()) {
            categoryId = vehicleOpt.get().getCategory().getCategoryId();
        } else {
            Optional<Rider> riderOpt = riderRepo.findByUserId(riderUserId);
            if (riderOpt.isPresent()) {
                categoryId = riderOpt.get().getCategory().getCategoryId();
            }
        }
        
        if (categoryId == null) {
            throw new ApiException("User does not have a category assigned via Vehicle or Rider");
        }

        // 3. Fetch all pending ride requests by category
        List<RideRequest> rideRequests = rideRequestRepo.findByCategory_CategoryId(categoryId);

        // 4. Filter ride requests within 10km pickup distance using Google API
        List<RideRequest> filteredRideRequests = new ArrayList<>();
        
        for (RideRequest ride : rideRequests) {
            try {
                DistanceMatrixResponse distanceData = mapServiceImpl.getDistanceMatrixData(
                        riderLocation.getLatitude(),
                        riderLocation.getLongitude(),
                        ride.getS_latitude(),
                        ride.getS_longitude()
                );
                if (distanceData.getDistanceKm() <= 10.0) { // Only within 10 km road distance
                    filteredRideRequests.add(ride);
                }
            } catch (Exception e) {
                // Log error and continue
                System.err.println("Failed to fetch distance for RideRequestId " + ride.getRideRequestId() + ": " + e.getMessage());
            }
        }

        // 5. Map to DTO
        List<RideRequestDto> dtoList = filteredRideRequests.stream().map(ride -> 
            RideRequestDto.builder()
                .rideRequestId(ride.getRideRequestId())
                .actualPrice(ride.getActualPrice())
                .d_latitude(ride.getD_latitude())
                .d_longitude(ride.getD_longitude())
                .d_Name(ride.getD_Name())
                .s_latitude(ride.getS_latitude())
                .s_longitude(ride.getS_longitude())
                .s_Name(ride.getS_Name())
                .build()
        ).collect(Collectors.toList());

      
        return dtoList;
    }

    
    @Override
    public PriceInfoDto determinePrice(RideRequestDto rideRequestDto, Integer userId, Integer categoryId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "User ID", userId));
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "Category ID", categoryId));

        if (user.getCurrentLocation() == null) {
            throw new ApiException("User's current location is not set.");
        }

        LocalDateTime locationTime = user.getCurrentLocation().getTimestamp();
        if (locationTime == null || locationTime.isBefore(LocalDateTime.now().minusMinutes(59))) {
            throw new ApiException("Please update your current location.");
        }

        double distance = 0.0;
        double duration = 0.0;

        try {
            DistanceMatrixResponse response = mapService.getDistanceMatrixData(
                user.getCurrentLocation().getLatitude(),
                user.getCurrentLocation().getLongitude(),
                rideRequestDto.getD_latitude(),
                rideRequestDto.getD_longitude()
            );

           distance= response.getDistanceKm();
            //distance = response.getDistance(); // already in km
            duration = response.getDurationMin(); // already in minutes

            System.out.println("Distance: " + distance + " km");
            System.out.println("Duration: " + duration + " mins");

        } catch (Exception e) {
            e.printStackTrace();
        }

       
     // Determine the state (province) based on user's current location
        String province;
        try {
            province = mapServiceImpl.getState(
                    user.getCurrentLocation().getLatitude(),
                    user.getCurrentLocation().getLongitude()
            );
        } catch (Exception e) {
            throw new ApiException("Error determining city.");
        }

        logger.info("Fetching pricing data for province: {} and category ID: {}", province, category.getCategoryId());
        // Fetch pricing data based on province and category
        Pricing pricing = pricingRepo.findByProvinceAndCategory_CategoryId(province, category.getCategoryId())
                .orElseThrow(() -> new ApiException("Pricing not available for the given province and category."));

        logger.info("Pricing data fetched successfully: {}", pricing);
        
        logger.info("New Distance: {}", distance);
        
        double generatedPrice = pricing.getBaseFare() + (pricing.getPerKmRate() * distance);

        return new PriceInfoDto(distance, generatedPrice,province,duration);
    }

    
    //rider le check garne full zoom , rider le ridereq accept garnu aaagi 
    @Override
    public RideInfoDto detailrideViewByRider(RideRequestDto rideRequestDto, Integer rideRequestId,Integer userId) {
    	
    	RideRequest pessenger = rideRequestRepo.findById(rideRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("RideRequest", "RideRequest ID", rideRequestId));
          
    	
   	 User rider = userRepo.findById(userId)
   	            .orElseThrow(() -> new ResourceNotFoundException("User", "User ID", userId));
      
   	 // Ensure user is in PASSENGER mode
     if (rider.getModes() != User.UserMode.RIDER) {
         throw new ApiException("must be in Rider mode .");
     }
   	 
	    // Check if the ride has already been approved by another rider [arko rider le approved gari sakyako raixa vane
	    if (pessenger.getStatus() == RideRequest.RideStatus.PESSENGER_APPROVED ||
	    		pessenger.getStatus() == RideRequest.RideStatus.REJECTED
	    		) {
	        throw new ApiException("This ride request has already been approved/rejected.");
	    }
   	
    if (rider.getCurrentLocation() == null) {
        throw new ApiException("Update Your current location.");
    }

    LocalDateTime locationTime = rider.getCurrentLocation().getTimestamp();
    if (locationTime == null || locationTime.isBefore(LocalDateTime.now().minusMinutes(59))) {
        throw new ApiException("Please update your current location.");
    }

    
    double pickupDistance = 0.0;
    double pickupDuration = 0.0;

    try {
        DistanceMatrixResponse response = mapService.getDistanceMatrixData(
        		rider.getCurrentLocation().getLatitude(),
                rider.getCurrentLocation().getLongitude(),
                pessenger.getS_latitude(),//pessenger cu
                pessenger.getS_longitude()
        );

        pickupDistance = response.getDistanceKm(); // distance km मा
        pickupDuration = response.getDurationMin(); // duration min मा
 // already in minutes

        System.out.println("Distance: " + pickupDistance + " km");
        System.out.println("Duration: " + pickupDuration + " mins");

    } catch (Exception e) {
        e.printStackTrace();
    }
    
    double dropDistance = 0.0;
    double dropDuration = 0.0;

    try {
        DistanceMatrixResponse response = mapService.getDistanceMatrixData(
        		pessenger.getS_latitude(),
        		pessenger.getS_longitude(), 
        		pessenger.getD_latitude(),
        		pessenger.getD_longitude()
        );

        dropDistance= response.getDistanceKm();
        //distance = response.getDistance(); // already in km
        dropDuration= response.getDurationMin(); // already in minutes

        System.out.println("Distance: " + dropDistance + " km");
        System.out.println("Duration: " + dropDuration + " mins");

    } catch (Exception e) {
        e.printStackTrace();
    }
 
    
   	return new RideInfoDto(pickupDistance,pickupDuration,dropDistance,dropDuration);
   	
   	
   }



    @Override
    public void deleteRideRequest(Integer rideRequestId) {
        RideRequest ride = rideRequestRepo.findById(rideRequestId)
            .orElseThrow(() -> new ResourceNotFoundException("RideRequest", "RideRequest ID", rideRequestId));
        rideRequestRepo.delete(ride);
       // webSocketController.sendRideStatusUpdate(ride);
    }

    @Override
    public RideRequestDto getRideRequestById(Integer rideRequestId) {
        RideRequest ride = rideRequestRepo.findById(rideRequestId)
            .orElseThrow(() -> new ResourceNotFoundException("RideRequest", "RideRequest ID", rideRequestId));
        return modelMapper.map(ride, RideRequestDto.class);
    }

    @Override
    public List<RideRequestDto> getRideRequestByUser(Integer userId) {
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "User ID", userId));
        List<RideRequest> rides = rideRequestRepo.findByUser(user);
        return rides.stream()
            .map(ride -> modelMapper.map(ride, RideRequestDto.class))
            .collect(Collectors.toList());
    }

    @Override
    public List<RideRequestDto> getAllRideRequests() {
        List<RideRequest> rides = rideRequestRepo.findAll();
        return rides.stream()
            .map(ride -> modelMapper.map(ride, RideRequestDto.class))
            .collect(Collectors.toList());
    }




}