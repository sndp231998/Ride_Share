package com.ride_share.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ride_share.controller.RideRequestWebSocketController;
import com.ride_share.entities.Category;
import com.ride_share.entities.Location;
import com.ride_share.entities.RideRequest;
import com.ride_share.entities.User;
import com.ride_share.entities.User.UserMode;
import com.ride_share.entities.Vehicle;
import com.ride_share.exceptions.ApiException;
import com.ride_share.exceptions.ResourceNotFoundException;
import com.ride_share.playoads.RideRequestDto;
import com.ride_share.playoads.UserDto;
import com.ride_share.playoads.VehicleDto;
import com.ride_share.repositories.CategoryRepo;
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
    private RideRequestWebSocketController webSocketController;


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
        Location currentLocation = user.getCurrentLocation();
        if (currentLocation == null) {
            throw new ApiException("User's current location is not set.");
        }
     // Validate if the location is recent (within 4-5 minutes)
        LocalDateTime locationTime = currentLocation.getTimestamp();
        LocalDateTime currentTime = LocalDateTime.now();

        // Check if the location is stale
        if (locationTime == null || locationTime.isBefore(currentTime.minusMinutes(5))) {
            throw new ApiException(" Please update your current location.");
        }
        
     // Fetch distance and time using MapService
        String response = mapService.getDistanceAndTime(
        		currentLocation.getLatitude(), 
                currentLocation.getLongitude(), 
                rideRequestDto.getDestination_lati(), 
                rideRequestDto.getDestination_long()
        		);
        		
        JSONObject jsonResponse = new JSONObject(response);
        int distanceInMeters = jsonResponse.getJSONArray("rows")
        	    .getJSONObject(0).getJSONArray("elements")
        	    .getJSONObject(0).getJSONObject("distance")
        	    .getInt("value");

        	// Convert the distance from meters to kilometers
        	double distanceInKm = distanceInMeters / 1000.0;
        	// Calculate the actual price using the distance in kilometers
        	
        	String city;
        	try {
        	city= mapServiceImpl.getCityName(currentLocation.getLatitude(), currentLocation.getLongitude());
        	}catch(Exception e) {
        		throw new ApiException("Error determining city.");
        	}
        	  // Calculate the actual price using the distance in kilometers and city-specific rates
            double baseFare;
            double perKmRate;
            switch (city) {
                case "Chitwan":
                    baseFare = 50;
                    perKmRate = 8;
                    break;
                case "Jhapa":
                    baseFare = 40;
                    perKmRate = 9;
                    break;
                case "Kathmandu":
                    baseFare = 60;
                    perKmRate = 10;
                    break;
                default:
                    throw new ApiException("Unsupported city for pricing.");
            }
        	double actualPrice = baseFare + (perKmRate * distanceInKm);
        	// Ensure the price falls within the acceptable range
            
        	double givenPrice;
        	try {
        	givenPrice= rideRequestDto.getActualPrice();
        	}catch(NumberFormatException e) {
        		throw new ApiException("Invalid price format.");
        	}
            
            if (givenPrice < actualPrice || givenPrice > actualPrice + 50) {
                throw new ApiException("Invalid price.");
            }
             // Create a new RideRequest
         RideRequest rideRequest = new RideRequest();
        rideRequest.setActualPrice(rideRequestDto.getActualPrice());
      //  rideRequest.setActualPrice(givenPrice);
        //----------yo destination---------
        rideRequest.setDestination_long(rideRequestDto.getDestination_long());
        rideRequest.setDestination_lati(rideRequestDto.getDestination_lati());
        
        //------------yo source-----------------------
       // rideRequest.setSource(rideRequestDto.getSource()); 
        // Set source using current location
        String source = "Lat: " + currentLocation.getLatitude() + ", Long: " + currentLocation.getLongitude();
        rideRequest.setSource(source);
        
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
        
     // Update fields only if provided
        if (rideRequestDto.getActualPrice() != 0) {
            rideRequest.setActualPrice(rideRequestDto.getActualPrice());
        }
        if (rideRequestDto.getSource() != null) {
            rideRequest.setSource(rideRequestDto.getSource());
        }
        if (rideRequestDto.getDestination_long() != null && rideRequestDto.getDestination_lati() != null) {
            rideRequest.setDestination_long(rideRequestDto.getDestination_long());
            rideRequest.setDestination_lati(rideRequestDto.getDestination_lati());
        }

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
  	        throw new IllegalStateException("This ride request has already been approved/reject by another rider.");
  	    }

  	 // Update actual price only if provided
  	//(rideRequestDto.getActualPrice() =yo current giving value ho hai
  	  if (rideRequestDto.getActualPrice() != 0) {
  	      ride.setActualPrice(rideRequestDto.getActualPrice());
  	  }    
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