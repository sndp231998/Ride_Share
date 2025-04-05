package com.ride_share.playoads;

import lombok.Data;

@Data
public class RideRequestResponseDto {
    private RideRequestDto rideRequestDto;
    private double generatedPrice;
    // getters and setters
}
