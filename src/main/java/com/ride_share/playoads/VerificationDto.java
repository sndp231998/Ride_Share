package com.ride_share.playoads;

import java.time.Instant;

import lombok.Data;

@Data
public class VerificationDto {
    private String otp;
    private Instant timestamp;
}
