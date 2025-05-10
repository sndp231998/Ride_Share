package com.ride_share.controller;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import com.ride_share.entities.RideRequest;
import com.ride_share.playoads.RideRequestDto;

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
}
