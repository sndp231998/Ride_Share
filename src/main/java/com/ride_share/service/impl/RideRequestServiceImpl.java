package com.ride_share.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
import com.ride_share.entities.User;
import com.ride_share.entities.User.UserMode;
import com.ride_share.entities.Vehicle;
import com.ride_share.exceptions.ApiException;
import com.ride_share.exceptions.ResourceNotFoundException;
import com.ride_share.playoads.DistanceMatrixResponse;
import com.ride_share.playoads.PriceInfoDto;
import com.ride_share.playoads.RideInfoDto;
import com.ride_share.playoads.RideRequestDto;

import com.ride_share.playoads.UserDto;
import com.ride_share.playoads.VehicleDto;
import com.ride_share.repositories.CategoryRepo;
import com.ride_share.repositories.PricingRepo;
import com.ride_share.repositories.RideRequestRepo;
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
    private VehicleRepo vehicleRepo;

    @Autowired
    private CategoryRepo categoryRepo;
    
    @Autowired
    private MapService mapService;
    
    @Autowired
    private PricingRepo pricingRepo;
    
    @Autowired
    private RideRequestWebSocketController webSocketController;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    // Existing methods (create, update, delete, get, etc.)
    
    
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
        try {
            DistanceMatrixResponse response = mapService.getDistanceMatrixData(
                user.getCurrentLocation().getLatitude(),
                user.getCurrentLocation().getLongitude(),
                rideRequestDto.getD_latitude(),
                rideRequestDto.getD_longitude()
            );

            distancekm = response.getDistanceKm();
            
    
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
        	//rideRequestDto.setGeneratedPrice(generatedPrice);

        	// Ensure the price falls within the acceptable range
            
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

      //Map<String, String> data = getDistanceMatrixData(
//	    27.686284293110297, 85.41387816149667, // source lat, lng
//	    27.715993329218495, 85.37965796635491  // destination lat, lng
//	);
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

       

        int distanceKm = mapService.getDistance(
                user.getCurrentLocation().getLatitude(),
                user.getCurrentLocation().getLongitude(),
                rideRequestDto.getD_latitude(),
                rideRequestDto.getD_longitude()
        );
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
    	
   	 User user = userRepo.findById(userId)
   	            .orElseThrow(() -> new ResourceNotFoundException("User", "User ID", userId));
      
   	RideRequest rideRequest = rideRequestRepo.findById(rideRequestId)
               .orElseThrow(() -> new ResourceNotFoundException("RideRequest", "RideRequest ID", rideRequestId));
         
   	
   	
   	if (user.getModes() != User.UserMode.RIDER) {  
	        throw new ApiException("User must be in RIDER mode to approved a ride request.");
	    }
	    // Check if the ride has already been approved by another rider [arko rider le approved gari sakyako raixa vane
	    if (rideRequest.getStatus() == RideRequest.RideStatus.PESSENGER_APPROVED ||
	    		rideRequest.getStatus() == RideRequest.RideStatus.REJECTED
	    		) {
	        throw new IllegalStateException("This ride request has already been approved/rejected.");
	    }
   	
    if (user.getCurrentLocation() == null) {
        throw new ApiException("User's current location is not set.");
    }

    LocalDateTime locationTime = user.getCurrentLocation().getTimestamp();
    if (locationTime == null || locationTime.isBefore(LocalDateTime.now().minusMinutes(59))) {
        throw new ApiException("Please update your current location.");
    }

    int pickupDistance = mapService.getDistance(
            user.getCurrentLocation().getLatitude(),
            user.getCurrentLocation().getLongitude(),
            rideRequest.getD_latitude(),
            rideRequest.getD_longitude()
    );
        
    int dropDistance=mapService.getDistance(
    		rideRequest.getS_latitude(),
    		rideRequest.getS_longitude(), 
    		rideRequest.getD_latitude(),
    		rideRequest.getD_longitude());
    
    
   	return new RideInfoDto(pickupDistance,dropDistance);
   	
   	
   }
//    
//    
    
    
    

    @Override
    public RideRequestDto updateRideRequest(RideRequestDto rideRequestDto, Integer rideRequestId) {
        RideRequest rideRequest = rideRequestRepo.findById(rideRequestId)
            .orElseThrow(() -> new ResourceNotFoundException("RideRequest", "RideRequest ID", rideRequestId));
        
        // Only allow updates if the ride status is PENDING
        if (rideRequest.getStatus() != RideRequest.RideStatus.PENDING) {
            throw new ApiException("Ride request can only be updated when the status is PENDING.");
        }
        
     // Update fields only if provided
        if (rideRequestDto.getActualPrice() != 0) {
            rideRequest.setActualPrice(rideRequestDto.getActualPrice());
        }
        if (rideRequestDto.getS_latitude() != 0 && rideRequestDto.getS_longitude() != 0) {
            rideRequest.setS_latitude(rideRequestDto.getS_latitude());
            rideRequest.setS_longitude(rideRequestDto.getS_longitude());
        }

//        if (rideRequestDto.getDestination().getD_longitude() != null && rideRequestDto.getDestination().getD_latitude() != null) {
//            rideRequest.setDestination_long(rideRequestDto.getDestination_long());
//            rideRequest.setDestination_lati(rideRequestDto.getDestination_lati());
//        }

        rideRequest.setStatus(RideRequest.RideStatus.PENDING);

        RideRequest updatedRide = rideRequestRepo.save(rideRequest);
        
        // ✅ Notify WebSocket clients
        webSocketController.sendRideStatusUpdate(updatedRide);

        return modelMapper.map(updatedRide, RideRequestDto.class);
    }
    // Method to fetch the list of rides who have sent requests for a RideRequest
    @Override
    public Set<UserDto> getRidersForRideRequest(Integer rideRequestId) {
        RideRequest rideRequest = rideRequestRepo.findById(rideRequestId)
            .orElseThrow(() -> new ResourceNotFoundException("RideRequest", "RideRequest ID", rideRequestId));

        // Check if the ride request is already approved or rejected
        if (rideRequest.getStatus() == RideRequest.RideStatus.PESSENGER_APPROVED|| 
            rideRequest.getStatus() == RideRequest.RideStatus.REJECTED) {
            throw new ApiException("This ride request is already approved or rejected. No more riders can be fetched.");
        }

       
        return rideRequest.getReqriders().stream()
            .map(rider -> {
                UserDto userDto = modelMapper.map(rider, UserDto.class);

                // Fetch and set only vehicle details (without fetching user separately)
                List<Vehicle> vehicles = vehicleRepo.findByUser(rider);
                Set<VehicleDto> vehicleDtos = vehicles.stream()
                    .map(vehicle -> {
                        VehicleDto vehicleDto = modelMapper.map(vehicle, VehicleDto.class);
                       // vehicleDto.setUser(null); // Remove user details to avoid duplication
                        return vehicleDto;
                    })
                    .collect(Collectors.toSet());

                userDto.setVehicles(vehicleDtos); // Set vehicle details
                return userDto;
            })
            .collect(Collectors.toSet());
    }

    // Method to approve a RideRequest by a rider
  //approved by Rider
  	@Override
  	public RideRequestDto approveRideRequestByRider(RideRequestDto rideRequestDto,Integer userId, Integer rideRequestId) {
  	    RideRequest ride = this.rideRequestRepo.findById(rideRequestId)
  	            .orElseThrow(() -> new ResourceNotFoundException("RideRequest", "RideRequest ID", rideRequestId));

  	    User user = this.userRepo.findById(userId)
  	            .orElseThrow(() -> new ResourceNotFoundException("User", "User ID", userId));

  	
//
  	    if (user.getModes() != User.UserMode.RIDER) {  
  	        throw new ApiException("User must be in RIDER mode to approved a ride request.");
  	    }
  	    // Check if the ride has already been approved by another rider [arko rider le approved gari sakyako raixa vane
  	    if (ride.getStatus() == RideRequest.RideStatus.PESSENGER_APPROVED ||
  	    		ride.getStatus() == RideRequest.RideStatus.REJECTED
  	    		) {
  	        throw new IllegalStateException("This ride request has already been approved/rejected.");
  	    }

  	  double reqpriceByrider=rideRequestDto.getReplacePessengerPrice();
  	  
  	double reqpriceBypessenger=ride.getActualPrice();
  	
  	
 // If rider's price is null or zero, set it to passenger's price
    if (reqpriceByrider == 0) {
        reqpriceByrider = reqpriceBypessenger;
        
        ride.setReplacePessengerPrice(ride.getActualPrice());
    }

    // Ensure rider's price is not less than passenger's price
    if (reqpriceByrider < reqpriceBypessenger) {
        throw new ApiException("The price offered by the rider cannot be less than the passenger's price.");
    }
    ride.setReplacePessengerPrice(reqpriceByrider);
    
        
  	  ride.getReqriders().add(user);// main point
  	    RideRequest approvedRide = this.rideRequestRepo.save(ride);
  	    
  	    
  	    return this.modelMapper.map(approvedRide, RideRequestDto.class);
  	}
  	
  	
  	//userId= jo rider ho; 
  	//riderRequestID= PESSENGER le garako req ko id
  	//currentuserid= jasle yo api hit gardai xa
  	@Override
  	public RideRequestDto approveRideRequestByPassenger(Integer rideRequestId, Integer userId, Integer currentUserId) {
  	    // Fetch the ride request using the rideRequestId
  	    RideRequest rideRequest = rideRequestRepo.findById(rideRequestId)
  	        .orElseThrow(() -> new ResourceNotFoundException("RideRequest", "RideRequest ID", rideRequestId));

  	    // ✅ Ensures that the user hitting the API (currentUserId) is the same passenger who created this ride request
  	    if (rideRequest.getUser().getId() != currentUserId) {
  	        throw new ApiException("You can only approve riders for your own ride requests.");
  	    }

  	    // Fetch the rider (userId) who is being approved
  	    User rider = userRepo.findById(userId)
  	        .orElseThrow(() -> new ResourceNotFoundException("User", "User ID", userId));

  	    // ✅ Ensures that the selected rider is actually in the list of users who requested this ride
  	    if (!rideRequest.getReqriders().contains(rider)) {
  	        throw new ApiException("This rider has not requested to join this ride.");
  	    }

  	    // ✅ Ensures that the selected user is in "RIDER" mode
  	    if (rider.getModes() != UserMode.RIDER) {
  	        throw new ApiException("Requested User is not in RIDE mode.");
  	    }

  	    // Fetch the current user (the one hitting this API) again to check their mode
  	    User currentUser = userRepo.findById(currentUserId)
  	        .orElseThrow(() -> new ResourceNotFoundException("User", "User ID", currentUserId));

  	    // ✅ Ensures that the current user is in "PESSENGER" mode
  	    if (currentUser.getModes() != UserMode.PESSENGER) {
  	        throw new ApiException("You Have To Be In Passenger Mode.");
  	    }

  	    // ✅ Updates the ride request with the approved rider's ID and changes status to PESSENGER_APPROVED
  	    rideRequest.setRidebookedId(String.valueOf(rider.getId()));
  	    rideRequest.setStatus(RideRequest.RideStatus.PESSENGER_APPROVED);

  	// ✅ Removes only the approved rider from the ride_request_riders table (for this rideRequestId)
  	  rideRequest.getReqriders().removeIf(r -> r.getId() == userId);


  	    // Save the updated ride request and return it as a DTO
  	    RideRequest approvedRide = rideRequestRepo.save(rideRequest);
  	// Notify WebSocket clients
  	  webSocketController.sendRideStatusUpdate(rideRequest);
  	    return modelMapper.map(approvedRide, RideRequestDto.class);
  	}

  	
 


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

   

}