package com.ride_share.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ride_share.entities.RideRequest;
import com.ride_share.entities.RiderApprovalRequest;
import com.ride_share.entities.RiderApprovalRequest.ApprovedStatus;
import com.ride_share.entities.User;
public interface RiderApprovalRequestRepo extends JpaRepository<RiderApprovalRequest, Integer> {

	List<RiderApprovalRequest> findByRideRequest_RideRequestIdAndStatus(Integer rideRequestId, RiderApprovalRequest.ApprovedStatus status);

	Optional<RiderApprovalRequest> findByUserAndRideRequest(User user, RideRequest ride);

	Optional<RiderApprovalRequest> findByRideRequest_RideRequestId(Integer rideRequestId);
	
	
	List<RiderApprovalRequest> findByStatusAndAddedDateBefore(
		    RiderApprovalRequest.ApprovedStatus status, LocalDateTime dateTime);
}



