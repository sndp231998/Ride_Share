package com.ride_share.controller;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import com.ride_share.entities.RideRequest;

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
}
