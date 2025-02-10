package com.ride_share.controller;



import com.ride_share.playoads.RideRequestDto;
import com.ride_share.playoads.UserDto;
import com.ride_share.service.RideRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/ride-requests")
public class RideRequestController {

    @Autowired
    private RideRequestService rideRequestService;

    // Create a new ride request
    @PostMapping("/user/{userId}")
    public ResponseEntity<RideRequestDto> createRideRequest(@RequestBody RideRequestDto rideRequestDto, @PathVariable Integer userId) {
        RideRequestDto createdRequest = rideRequestService.createRideRequest(rideRequestDto, userId);
        return ResponseEntity.ok(createdRequest);
    }

    // Approve a ride request by Rider
 // Approve a ride request by Rider (userId is passed as a path variable)
    @PutMapping("/{userId}/approve-rider/{rideRequestId}")
    public ResponseEntity<RideRequestDto> approveRideByRider(@RequestBody RideRequestDto rideRequestDto,
    	                                                         	@PathVariable Integer userId,
                                                             @PathVariable Integer rideRequestId
                                                             ) {
        RideRequestDto approvedRequest = rideRequestService.approveRideRequestByRider(rideRequestDto, rideRequestId, userId);
        return ResponseEntity.ok(approvedRequest);
    }

    @GetMapping("/{rideRequestId}/riders")
    public ResponseEntity<Set<UserDto>> getRidersForRideRequest(@PathVariable Integer rideRequestId) {
        Set<UserDto> riders = rideRequestService.getRidersForRideRequest(rideRequestId);
        return ResponseEntity.ok(riders);
    }
    // Approve a ride request by Passenger
    //rideRequestId; userId=riderId/who req for pessenger for ride and currentUserId
    @PutMapping("/{rideRequestId}/approve-passenger/user/{userId}/currentuser/{currentUserId}")
    public ResponseEntity<RideRequestDto> approveRideByPassenger(@PathVariable Integer rideRequestId, 
    		@PathVariable Integer userId, @PathVariable Integer currentUserId) {
        RideRequestDto approvedRequest = rideRequestService.approveRideRequestByPassenger(rideRequestId,userId,currentUserId);
        return ResponseEntity.ok(approvedRequest);
    }

    // Reject a ride request
    @PutMapping("/{rideRequestId}/reject")
    public ResponseEntity<RideRequestDto> rejectRideRequest(@PathVariable Integer rideRequestId) {
        RideRequestDto rejectedRequest = rideRequestService.rejectRideRequest(rideRequestId);
        return ResponseEntity.ok(rejectedRequest);
    }

    // Get ride request by ID
    @GetMapping("/{rideRequestId}")
    public ResponseEntity<RideRequestDto> getRideRequestById(@PathVariable Integer rideRequestId) {
        RideRequestDto rideRequest = rideRequestService.getRideRequestById(rideRequestId);
        return ResponseEntity.ok(rideRequest);
    }

    // Get all ride requests
    @GetMapping("/")
    public ResponseEntity<List<RideRequestDto>> getAllRideRequests() {
        List<RideRequestDto> rideRequests = rideRequestService.getAllRideRequests();
        return ResponseEntity.ok(rideRequests);
    }

    // Delete a ride request
    @DeleteMapping("/{rideRequestId}")
    public ResponseEntity<String> deleteRideRequest(@PathVariable Integer rideRequestId) {
        rideRequestService.deleteRideRequest(rideRequestId);
        return ResponseEntity.ok("Ride request deleted successfully");
    }
    
    // Update Ride Request (Only if Status is PENDING)
    @PutMapping("/{rideRequestId}")
    public ResponseEntity<RideRequestDto> updateRideRequest(
            @PathVariable Integer rideRequestId,
            @RequestBody RideRequestDto rideRequestDto) {

        RideRequestDto updatedRide = rideRequestService.updateRideRequest(rideRequestDto, rideRequestId);
        return ResponseEntity.ok(updatedRide);
    }
}
