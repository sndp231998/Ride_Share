package com.ride_share.playoads;

import lombok.Data;

@Data
public class ResetPasswordRequestDto {

	private String emailOrMobile;
    private String otp;
    private String newPassword;
}
