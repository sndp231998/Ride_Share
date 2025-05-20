package com.ride_share.controller;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import com.ride_share.entities.RideRequest;
import com.ride_share.entities.RiderApprovalRequest;
import com.ride_share.playoads.RideRequestDto;
import com.ride_share.playoads.RideRequestResponseDto;
import com.ride_share.playoads.RiderApprovalRequestDto;

@Controller
public class RideRequestWebSocketController {

	@Autowired
    private SimpMessagingTemplate messagingTemplate;
	
	 public void sendEligibleRiders(List<Integer> userIds, RideRequestDto rideRequestDto) {
	        Map<String, Object> payload = new HashMap<>();
	        payload.put("userIds", userIds);
	        payload.put("rideRequest", rideRequestDto);

	        // Send to a **common topic**
	        messagingTemplate.convertAndSend("/topic/eligible-riders", payload);
	    }

	 @MessageMapping("/send/message")
	 @SendTo("/topic/messages")
	 public String send(String message) {
	     return message;
	 }

	
	//main riderequest reject// by pessenger
	 public void sendRideRejected(RideRequestDto dto, Integer rideRequestId) {
		    messagingTemplate.convertAndSend("/topic/ride-rejected/" + rideRequestId, dto);
		}

	 
	 //final approved for ride......
	 public void sendPassengerApproved(RideRequestDto dto,Integer rideRequestId) {
	        messagingTemplate.convertAndSend("/topic/passenger-approved/"+rideRequestId, dto);
	    }
	 
	
	 //approval reject by pessenger ;
	 public void notifyPassengerRejectedRider(RiderApprovalRequestDto dto, Integer riderAppId) {
		    messagingTemplate.convertAndSend("/topic/passenger-rejected-rider/" + riderAppId, dto);
		}
	 
	 //pessenger lai janxa yo every rider ko request [create rideApproval
	 public void notifyUpdatedRiderList(Set<RideRequestResponseDto> updatedRiders,
				Integer rideRequestId) {
		 messagingTemplate.convertAndSend("/topic/rider-approvals/" + rideRequestId,updatedRiders);
		}

	 
	

	

}
