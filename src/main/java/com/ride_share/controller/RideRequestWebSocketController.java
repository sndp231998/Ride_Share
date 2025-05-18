package com.ride_share.controller;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Set;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import com.ride_share.entities.RideRequest;
import com.ride_share.entities.RiderApprovalRequest;
import com.ride_share.playoads.RideRequestDto;
import com.ride_share.playoads.RideRequestResponseDto;

@Controller
public class RideRequestWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    public RideRequestWebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/ride-updates")
    @SendTo("/topic/rides")
    public RideRequest sendRideUpdates(RideRequest rideRequest) {
        return rideRequest;
    }

    public void sendRideStatusUpdate(RideRequest rideRequest) {
        messagingTemplate.convertAndSend("/topic/rides", rideRequest);
    }

    //for rider all rides with in 10km around
	public void sendSortedRideRequestListUpdate(int riderUserId, List<RideRequestDto>  rideRequests) {
		messagingTemplate.convertAndSend("/topic/sorted-ride-requests/" + riderUserId, rideRequests);
		///to access=topic/sorted-ride-requests/{riderUserId}
		
	}
	// localhost:...api/v1/riderAppReq/{rideRequestId}/pending-riders
	//rider le approved garne bitike harna milxa+ get all gar.r harda pn hunxa
	public void sendRiderListForRideRequest(Integer rideRequestId, Set<RideRequestResponseDto> riderList) {
	    messagingTemplate.convertAndSend("/topic/ride-riders/" + rideRequestId, riderList);
	}

	//approval reject by pessenger
	public void sendPessengerRejectedApp(RiderApprovalRequest ride) {
	    messagingTemplate.convertAndSend("/topic/ride-rejected-pess", ride);
	}
	
	//main ride request reject by pessenger
//	public void sendRejectedRide(RideRequest rideRequest) {
//	    messagingTemplate.convertAndSend("/topic/ride-rejected", rideRequest);
//	}
	public void sendRejectedRide(RideRequestDto rideRequestDto) {
	    messagingTemplate.convertAndSend("/topic/rejectedRide", rideRequestDto);
	}

	public void sendPassengerApprovedRide(RideRequest rideRequest) {
	    messagingTemplate.convertAndSend("/topic/ride-passenger-approved", rideRequest);
	}

	public void sendRiderApprovedRide(RideRequest rideRequest) {
	    messagingTemplate.convertAndSend("/topic/ride-rider-approved", rideRequest);
	}

	
	
	
}
