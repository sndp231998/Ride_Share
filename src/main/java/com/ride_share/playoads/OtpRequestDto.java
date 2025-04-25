package com.ride_share.playoads;

import lombok.Data;

@Data
public class OtpRequestDto {
	  private String emailOrMobile;
	    private String otp;
}
