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
	
	
	//main riderequest reject// by pessenger
	 public void sendRideRejected(RideRequestDto dto) {
	        messagingTemplate.convertAndSend("/topic/ride-rejected", dto);
	    }
	 
	 //final approved for ride......
	 public void sendPassengerApproved(RideRequestDto dto) {
	        messagingTemplate.convertAndSend("/topic/passenger-approved", dto);
	    }
	 
	
	 
	 //-------------------------Approval reject-----pessenger le rider ko approval lai reject garako update-----------
	 public void notifyPassengerRejectedRider(RiderApprovalRequestDto dto) {
		    messagingTemplate.convertAndSend("/topic/passenger-rejected-rider", dto);
		}

	 ///api/v1/riderAppReq/43/user/34==> riderAppReq/riderequestId/user/userId->riderId
	 //---------approval garxa rider le pessenger ko ride request ma--[show for pessenger side****
	 public void notifyUpdatedRiderList(Set<RideRequestResponseDto> updatedRiders, Integer rideRequestId) {
		    messagingTemplate.convertAndSend("/topic/rider-approvals/" + rideRequestId, updatedRiders);
		}
	 //In frontend (JS or React or whatever), make sure the passenger is listening to:
	// /topic/rider-approvals/{rideRequestId}

	 
	 //[ confuse ] no need to look right now****
	 public void sendRiderApproved(RideRequestDto dto) {
	        messagingTemplate.convertAndSend("/topic/rider-approved", dto);
	    }

	public void sendRideStatusUpdate(RideRequest savedRideReq) {
		// TODO Auto-generated method stub
		messagingTemplate.convertAndSend("/topic/ride-request",savedRideReq);
	}
}
