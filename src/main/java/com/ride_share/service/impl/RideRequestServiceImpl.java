package com.ride_share.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ride_share.entities.RideRequest;
import com.ride_share.entities.User;
import com.ride_share.entities.Vehicle;
import com.ride_share.exceptions.ResourceNotFoundException;
import com.ride_share.playoads.RideRequestDto;
import com.ride_share.playoads.UserDto;
import com.ride_share.playoads.VehicleDto;
import com.ride_share.repositories.RideRequestRepo;
import com.ride_share.repositories.UserRepo;
import com.ride_share.repositories.VehicleRepo;
import com.ride_share.service.RideRequestService;

@Service
public class RideRequestServiceImpl implements RideRequestService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private RideRequestRepo rideRequestRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private VehicleRepo vehicleRepo;



    // Method to fetch the list of riders who have sent requests for a RideRequest
    @Override
    public Set<UserDto> getRidersForRideRequest(Integer rideRequestId) {
        RideRequest rideRequest = rideRequestRepo.findById(rideRequestId)
            .orElseThrow(() -> new ResourceNotFoundException("RideRequest", "RideRequest ID", rideRequestId));

        return rideRequest.getReqriders().stream()
            .map(rider -> {
                UserDto userDto = modelMapper.map(rider, UserDto.class);

                // Fetch and set only vehicle details (without fetching user separately)
                List<Vehicle> vehicles = vehicleRepo.findByUser(rider);
                Set<VehicleDto> vehicleDtos = vehicles.stream()
                    .map(vehicle -> {
                        VehicleDto vehicleDto = modelMapper.map(vehicle, VehicleDto.class);
                        vehicleDto.setUser(null); // Remove user details to avoid duplication
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
  	        throw new IllegalStateException("User must be in RIDER mode to approved a ride request.");
  	    }
  	    // Check if the ride has already been approved by another rider [arko rider le approved gari sakyako raixa vane
  	    if (ride.getStatus() == RideRequest.RideStatus.RIDER_APPROVED) {
  	        throw new IllegalStateException("This ride request has already been approved by another rider.");
  	    }

  	    // Allow rider to update price during approval
  	   // ride.setActualPrice(rideRequestDto.getActualPrice());
  	    //ride.setStatus(RideRequest.RideStatus.RIDER_APPROVED);
  	 // Update actual price only if provided
  	    if (rideRequestDto.getActualPrice() != null && !rideRequestDto.getActualPrice().isEmpty()) {
  	        ride.setActualPrice(rideRequestDto.getActualPrice());
  	    }
  	    
  	  ride.getReqriders().add(user);
  	    RideRequest approvedRide = this.rideRequestRepo.save(ride);
  	    return this.modelMapper.map(approvedRide, RideRequestDto.class);
  	}
//    @Override
//    public RideRequestDto approveRideRequestByRider(RideRequestDto rideRequestDto, Integer userId, Integer rideRequestId) {
//        // Fetch the ride request
//        RideRequest ride = rideRequestRepo.findById(rideRequestId)
//            .orElseThrow(() -> new ResourceNotFoundException("RideRequest", "RideRequest ID", rideRequestId));
//
//
//        if (rideRequestDto.getActualPrice() != null && !rideRequestDto.getActualPrice().isEmpty()) {
//            ride.setActualPrice(rideRequestDto.getActualPrice());
//        }
//        // Fetch the rider (approving user)
//        User riderUser = userRepo.findById(userId)
//            .orElseThrow(() -> new ResourceNotFoundException("User", "User ID", userId));
//
//        // Validate if the user is a rider
//        if (riderUser.getModes() != User.UserMode.RIDER) {
//            throw new IllegalStateException("User must be in RIDER mode to approve a ride request.");
//        }
//
//       
//        ride.getReqriders().add(riderUser);
//
//
//        // Save the updated ride request
//        RideRequest approvedRide = rideRequestRepo.save(ride);
//
//        // Map the ride request to DTO
//        RideRequestDto responseDto = modelMapper.map(approvedRide, RideRequestDto.class);
//
////        // Set both users and vehicle
////        responseDto.setRequestedByUser(modelMapper.map(requesterUser, UserDto.class)); // The requester
////        responseDto.setApprovedByUser(modelMapper.map(riderUser, UserDto.class)); // The rider
////        responseDto.setVehicle(vehicleDto);
//
//        return responseDto;
//    }

    // Existing methods (create, update, delete, get, etc.)
    @Override
    public RideRequestDto createRideRequest(RideRequestDto rideRequestDto, Integer userId) {
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "User ID", userId));

        // Ensure user is in PASSENGER mode
        if (user.getModes() != User.UserMode.PESSENGER) {
            throw new IllegalStateException("User must be in PESSENGER mode to create a ride request.");
        }

        RideRequest ride = modelMapper.map(rideRequestDto, RideRequest.class);
        ride.setAddedDate(LocalDateTime.now());
        ride.setStatus(RideRequest.RideStatus.PENDING);
        ride.setUser(user); // Link the ride request to the user

        RideRequest savedRideReq = rideRequestRepo.save(ride);
        return modelMapper.map(savedRideReq, RideRequestDto.class);
    }

    @Override
    public RideRequestDto updateRideRequest(RideRequestDto rideRequestDto, Integer rideRequestId) {
        RideRequest req = rideRequestRepo.findById(rideRequestId)
            .orElseThrow(() -> new ResourceNotFoundException("RideRequest", "RideRequest ID", rideRequestId));
        req.setDestination(rideRequestDto.getDestination());
        req.setSource(rideRequestDto.getSource());
        req.setActualPrice(rideRequestDto.getActualPrice());
        RideRequest updatedRide = rideRequestRepo.save(req);
        return modelMapper.map(updatedRide, RideRequestDto.class);
    }

    @Override
    public void deleteRideRequest(Integer rideRequestId) {
        RideRequest ride = rideRequestRepo.findById(rideRequestId)
            .orElseThrow(() -> new ResourceNotFoundException("RideRequest", "RideRequest ID", rideRequestId));
        rideRequestRepo.delete(ride);
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
        return modelMapper.map(rejectedRide, RideRequestDto.class);
    }

    @Override
    public RideRequestDto approveRideRequestByPassenger(Integer rideRequestId) {
        RideRequest ride = rideRequestRepo.findById(rideRequestId)
            .orElseThrow(() -> new ResourceNotFoundException("RideRequest", "RideRequest ID", rideRequestId));

        // Passenger can approve only if the ride is already approved by a rider
        if (ride.getStatus() != RideRequest.RideStatus.RIDER_APPROVED) {
            throw new IllegalStateException("The ride request must be approved by a rider before passenger approval.");
        }

        ride.setStatus(RideRequest.RideStatus.PESSENGER_PAPPROVED);
        RideRequest approvedRide = rideRequestRepo.save(ride);
        return modelMapper.map(approvedRide, RideRequestDto.class);
    }
//  // Method to allow riders to send requests for a RideRequest
//  @Override
//  public void sendRideRequest(Integer rideRequestId, Integer riderId) {
//      RideRequest rideRequest = rideRequestRepo.findById(rideRequestId)
//          .orElseThrow(() -> new ResourceNotFoundException("RideRequest", "RideRequest ID", rideRequestId));
//
//      User rider = userRepo.findById(riderId)
//          .orElseThrow(() -> new ResourceNotFoundException("User", "User ID", riderId));
//
//      // Ensure the user is a rider
//      if (rider.getModes() != User.UserMode.RIDER) {
//          throw new IllegalStateException("Only users in RIDER mode can send ride requests.");
//      }
//
//      // Add the rider to the ride request
//      rideRequest.getReqriders().add(rider);
//      rideRequestRepo.save(rideRequest);
//  }
}