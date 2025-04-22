package com.ride_share.service.impl;

import java.time.LocalDateTime;
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
import com.ride_share.entities.RideRequest;
import com.ride_share.entities.RiderApprovalRequest;
import com.ride_share.entities.RiderApprovalRequest.ApprovedStatus;
import com.ride_share.entities.User;
import com.ride_share.entities.User.UserMode;
import com.ride_share.entities.Vehicle;
import com.ride_share.exceptions.ApiException;
import com.ride_share.exceptions.ResourceNotFoundException;
import com.ride_share.playoads.DistanceMatrixResponse;
import com.ride_share.playoads.PriceInfoDto;
import com.ride_share.playoads.RideInfoDto;
import com.ride_share.playoads.RideRequestDto;
import com.ride_share.playoads.RiderApprovalRequestDto;
import com.ride_share.playoads.UserDto;
import com.ride_share.playoads.VehicleDto;
import com.ride_share.repositories.CategoryRepo;
import com.ride_share.repositories.PricingRepo;
import com.ride_share.repositories.RideRequestRepo;
import com.ride_share.repositories.RiderApprovalRequestRepo;
import com.ride_share.repositories.UserRepo;
import com.ride_share.repositories.VehicleRepo;
import com.ride_share.service.MapService;
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
    private CategoryRepo categoryRepo;
    
    @Autowired
    private MapService mapService;
    
    @Autowired
    private PricingRepo pricingRepo;
    
    @Autowired
    private RiderApprovalRequestRepo riderApprovalRequestRepo;
    
    @Autowired
    private RideRequestWebSocketController webSocketController;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    // Existing methods (create, update, delete, get, etc.)
    
    

//    @Override
//    public List<RiderApprovalRequestDto> getAllPendingApprovalRequestsByRideRequestId(Integer rideRequestId) {
//    	List<RiderApprovalRequest> requests = riderApprovalRequestRepo
//    	        .findAllByRideRequestRideRequestIdAndStatus(rideRequestId, ApprovedStatus.PENDING);
//
//    	    return requests.stream()
//    	        .map(request -> modelMapper.map(request, RiderApprovalRequestDto.class))
//    	        .collect(Collectors.toList());
//    }
//    
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

      // destination --saved
         rideRequest.setD_latitude(rideRequestDto.getD_latitude());
         rideRequest.setD_longitude(rideRequestDto.getD_longitude());
        rideRequest.setActualPrice(givenPrice);
    rideRequest.setTotal_Km(distancekm);
    rideRequest.setD_Name(destinationAdd);
    rideRequest.setS_Name(sourceAdd);
        //------------yo source-saved----------------------
       
        rideRequest.setS_latitude(user.getCurrentLocation().getLatitude());
        rideRequest.setS_longitude(user.getCurrentLocation().getLongitude());
        //source.setTimestamp(LocalDateTime.now());
        
       rideRequest.setAddedDate(LocalDateTime.now());
        rideRequest.setStatus(RideRequest.RideStatus.PENDING);
        rideRequest.setUser(user); // Link the ride request to the user
        rideRequest.setCategory(category);
        // Save the ride request
        RideRequest savedRideReq = rideRequestRepo.save(rideRequest);
        
        // Send WebSocket notification
        webSocketController.sendRideStatusUpdate(savedRideReq);

        return modelMapper.map(savedRideReq, RideRequestDto.class);
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
        rideRequest.setD_Name(destinationAdd);
        rideRequest.setTotal_Km(distancekm);
        rideRequest.setStatus(RideRequest.RideStatus.PENDING); // still pending

        RideRequest updatedRide = rideRequestRepo.save(rideRequest);

        // Notify clients
        webSocketController.sendRideStatusUpdate(updatedRide);

        return modelMapper.map(updatedRide, RideRequestDto.class);
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
    	
    	RideRequest rideRequest = rideRequestRepo.findById(rideRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("RideRequest", "RideRequest ID", rideRequestId));
          
    	
   	 User user = userRepo.findById(userId)
   	            .orElseThrow(() -> new ResourceNotFoundException("User", "User ID", userId));
      
   	 // Ensure user is in PASSENGER mode
     if (user.getModes() != User.UserMode.RIDER) {
         throw new ApiException("must be in Rider mode .");
     }
   	 
	    // Check if the ride has already been approved by another rider [arko rider le approved gari sakyako raixa vane
	    if (rideRequest.getStatus() == RideRequest.RideStatus.PESSENGER_APPROVED ||
	    		rideRequest.getStatus() == RideRequest.RideStatus.REJECTED
	    		) {
	        throw new IllegalStateException("This ride request has already been approved/rejected.");
	    }
   	
    if (user.getCurrentLocation() == null) {
        throw new ApiException("Update Your current location.");
    }

    LocalDateTime locationTime = user.getCurrentLocation().getTimestamp();
    if (locationTime == null || locationTime.isBefore(LocalDateTime.now().minusMinutes(59))) {
        throw new ApiException("Please update your current location.");
    }

    
    double pickupDistance = 0.0;
    double pickupDuration = 0.0;

    try {
        DistanceMatrixResponse response = mapService.getDistanceMatrixData(
        		user.getCurrentLocation().getLatitude(),
                user.getCurrentLocation().getLongitude(),
                rideRequest.getD_latitude(),
                rideRequest.getD_longitude()
        );

        pickupDistance= response.getDistanceKm();
        //distance = response.getDistance(); // already in km
        pickupDistance = response.getDurationMin(); // already in minutes

        System.out.println("Distance: " + pickupDistance + " km");
        System.out.println("Duration: " + pickupDuration + " mins");

    } catch (Exception e) {
        e.printStackTrace();
    }
    
    double dropDistance = 0.0;
    double dropDuration = 0.0;

    try {
        DistanceMatrixResponse response = mapService.getDistanceMatrixData(
        		rideRequest.getS_latitude(),
        		rideRequest.getS_longitude(), 
        		rideRequest.getD_latitude(),
        		rideRequest.getD_longitude()
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
//    
//    
    
  

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
  	    
  	    
  	    return this.modelMapper.map(approvedRide, RideRequestDto.class);
  	}
  	
  	
  	//userId= jo rider ho; //pessenger le choose garxa 
  	//riderRequestID= PESSENGER le garako req ko id
  	//currentuserid= jasle yo api hit gardai xa
//  	@Override
//  	public RideRequestDto approveRideRequestByPassenger(Integer rideRequestId, Integer userId, Integer currentUserId) {
//  		logger.info("Starting approval process with rideRequestId: {}, userId: {}, currentUserId: {}", rideRequestId, userId, currentUserId);
//  	    // Fetch the ride request using the rideRequestId
//  	    RideRequest rideRequest = rideRequestRepo.findById(rideRequestId)
//  	        .orElseThrow(() -> new ResourceNotFoundException("RideRequest", "RideRequest ID", rideRequestId));
//  	  User user = userRepo.findById(userId)
//  		    .orElseThrow(() -> new ResourceNotFoundException("User", "User ID", userId));
//
//         
//  	  RiderApprovalRequest rowdata = riderApprovalRepo.findByUserAndRideRequest(user, rideRequest)
//  		    .orElseThrow(() -> new ResourceNotFoundException("RideApprovalRequest", "RideRequest ID", rideRequestId));
//
//   
//  	  // ✅ riderequest.getuser.getid= riderequest kasle create garako =====current user
//	    //(currentUserId) is the same passenger who created this ride request
//	    if (rideRequest.getUser().getId() != currentUserId) {
//	        throw new ApiException("You can only approve riders for your own ride requests.");
//	    }
//  	  
//  	  if(rowdata.getStatus()==RiderApprovalRequest.ApprovedStatus.REJECTED ||
//  			  rowdata.getStatus()==RiderApprovalRequest.ApprovedStatus.REJECTED) {
//  		  throw new ApiException("This is already approved/reject");
//  	  }
//  		  
//  	  
//  	    //riderequestid +rider ko id 
//  	  Optional<RiderApprovalRequest> approvalRequest = riderApprovalRepo.findByRideRequestRideRequestIdAndUserId(rideRequestId, userId);
//  		    //.findByRideRequestIdAndRiderId(rideRequestId, userId);
//
//  		if (!approvalRequest.isPresent()) {
//  		    throw new ApiException("This rider has not requested to join your ride.");
//  		}
//  		
//  		rideRequest.setActualPrice(rowdata.getProposed_price()); //rider le garako proposed_price pessenger ko actualprice sg replace grako
//  		rideRequest.setApprovedriderId(userId);//pessenger le choosed garako rider ko id save garako
//  		RiderApprovalRequest request = approvalRequest.get();
//  		request.setStatus(RiderApprovalRequest.ApprovedStatus.PESSENGER_APPROVED);
//  	   
//  		riderApprovalRepo.save(request);
//
//
//  	    // Save the updated ride request and return it as a DTO
//  	    RideRequest approvedRide = rideRequestRepo.save(rideRequest);
//  	// Notify WebSocket clients
//  	  webSocketController.sendRideStatusUpdate(rideRequest);
//  	    return modelMapper.map(approvedRide, RideRequestDto.class);
//  	}

  	
  
    

    // Method to fetch the list of rides who have sent requests for a RideRequest
//    @Override
//    public Set<UserDto> getRidersForRideRequest(Integer rideRequestId) {
//        RideRequest rideRequest = rideRequestRepo.findById(rideRequestId)
//            .orElseThrow(() -> new ResourceNotFoundException("RideRequest", "RideRequest ID", rideRequestId));
//
//        // Check if the ride request is already approved or rejected
//        if (rideRequest.getStatus() == RideRequest.RideStatus.PESSENGER_APPROVED|| 
//            rideRequest.getStatus() == RideRequest.RideStatus.REJECTED) {
//            throw new ApiException("This ride request is already approved or rejected. No more riders can be fetched.");
//        }
//
//       
//        return rideRequest.getReqriders().stream()
//            .map(rider -> {
//                UserDto userDto = modelMapper.map(rider, UserDto.class);
//
//                // Fetch and set only vehicle details (without fetching user separately)
//                List<Vehicle> vehicles = vehicleRepo.findByUser(rider);
//                Set<VehicleDto> vehicleDtos = vehicles.stream()
//                    .map(vehicle -> {
//                        VehicleDto vehicleDto = modelMapper.map(vehicle, VehicleDto.class);
//                       // vehicleDto.setUser(null); // Remove user details to avoid duplication
//                        return vehicleDto;
//                    })
//                    .collect(Collectors.toSet());
//
//                userDto.setVehicles(vehicleDtos); // Set vehicle details
//                return userDto;
//            })
//            .collect(Collectors.toSet());
//    }


    @Override
    public void deleteRideRequest(Integer rideRequestId) {
        RideRequest ride = rideRequestRepo.findById(rideRequestId)
            .orElseThrow(() -> new ResourceNotFoundException("RideRequest", "RideRequest ID", rideRequestId));
        rideRequestRepo.delete(ride);
        webSocketController.sendRideStatusUpdate(ride);
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

    @Override
    public RideRequestDto rejectRideRequest(Integer rideRequestId) {
        RideRequest ride = rideRequestRepo.findById(rideRequestId)
            .orElseThrow(() -> new ResourceNotFoundException("RideRequest", "RideRequest ID", rideRequestId));
        ride.setStatus(RideRequest.RideStatus.REJECTED);
        RideRequest rejectedRide = rideRequestRepo.save(ride);
     // ✅ Notify WebSocket clients
        webSocketController.sendRideStatusUpdate(rejectedRide);

        return modelMapper.map(rejectedRide, RideRequestDto.class);
    }
//
//	@Override
//  	public RideRequestDto approveRideRequestByPassenger(Integer rideRequestId, Integer userId, Integer currentUserId) {
//  	    // Fetch the ride request using the rideRequestId
//  	    RideRequest rideRequest = rideRequestRepo.findById(rideRequestId)
//  	        .orElseThrow(() -> new ResourceNotFoundException("RideRequest", "RideRequest ID", rideRequestId));
//
//  	    // ✅ riderequest.getuser.getid= riderequest kasle create garako =====current user
//  	    //(currentUserId) is the same passenger who created this ride request
//  	    if (rideRequest.getUser().getId() != currentUserId) {
//  	        throw new ApiException("You can only approve riders for your own ride requests.");
//  	    }
//
//  	    // Fetch the rider (userId) who is being approved
//  	    User rider = userRepo.findById(userId)
//  	        .orElseThrow(() -> new ResourceNotFoundException("User", "User ID", userId));
//
//  	    // ✅ Ensures that the selected rider is actually in the list of users who requested this ride
//  	    if (!rideRequest.getReqriders().contains(rider)) {
//  	        throw new ApiException("This rider has not requested to join this ride.");
//  	    }
//
//  	    // ✅ Ensures that the selected user is in "RIDER" mode
//  	    if (rider.getModes() != UserMode.RIDER) {
//  	        throw new ApiException("Requested User is not in RIDE mode.");
//  	    }
//
//  	    // Fetch the current user (the one hitting this API) again to check their mode
//  	    User currentUser = userRepo.findById(currentUserId)
//  	        .orElseThrow(() -> new ResourceNotFoundException("User", "User ID", currentUserId));
//
//  	    // ✅ Ensures that the current user is in "PESSENGER" mode
//  	    if (currentUser.getModes() != UserMode.PESSENGER) {
//  	        throw new ApiException("You Have To Be In Passenger Mode.");
//  	    }
//
//  	    // ✅ Updates the ride request with the approved rider's ID and changes status to PESSENGER_APPROVED
//  	    rideRequest.setRidebookedId(String.valueOf(rider.getId()));
//  	    rideRequest.setStatus(RideRequest.RideStatus.PESSENGER_APPROVED);
//
//  	// ✅ Removes only the approved rider from the ride_request_riders table (for this rideRequestId)
//  	  rideRequest.getReqriders().removeIf(r -> r.getId() == userId);
//
//
//  	    // Save the updated ride request and return it as a DTO
//  	    RideRequest approvedRide = rideRequestRepo.save(rideRequest);
//  	// Notify WebSocket clients
//  	  webSocketController.sendRideStatusUpdate(rideRequest);
//  	    return modelMapper.map(approvedRide, RideRequestDto.class);
//  	}

	

	@Override
	public RideRequestDto approveRideRequestByPassenger(Integer rideRequestId, Integer userId, Integer currentUserId) {
		// TODO Auto-generated method stub
		return null;
	}

	

//    @Override
//    public Set<UserDto> getRidersForRideRequest(Integer rideRequestId) {
//        List<RiderApprovalRequest> pendingApprovals = riderApprovalRequestRepo
//            .findByRideRequest_RideRequestIdAndStatus(
//                rideRequestId,
//                RiderApprovalRequest.ApprovedStatus.PENDING
//            );
//
//        return pendingApprovals.stream()
//            .map(approval -> modelMapper.map(approval.getUser(), UserDto.class))
//            .collect(Collectors.toSet());
//    }

   
//	@Override
//	public RideRequestDto approveRideRequestByPassenger(Integer rideRequestId, Integer userId, Integer currentUserId) {
//		// TODO Auto-generated method stub
//		return null;
//	}


}