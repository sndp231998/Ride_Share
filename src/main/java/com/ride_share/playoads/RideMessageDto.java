package com.ride_share.playoads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideMessageDto {

	Integer userId;
	Integer rideRequestId;
	private double longitude;
    private double latitude;
    private String type;
}
