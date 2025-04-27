package com.ride_share.playoads;

import com.ride_share.entities.RideRequest;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RideRequestWithDistanceDto {
    private RideRequest rideRequest;
    private double distance;
}

