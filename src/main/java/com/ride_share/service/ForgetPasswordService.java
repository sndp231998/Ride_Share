package com.ride_share.service;

import com.ride_share.entities.ForgetPassword;
import com.ride_share.playoads.ForgetPasswordDto;

public interface ForgetPasswordService {
	//create for reg
	ForgetPassword createForget(ForgetPasswordDto pas);
	
	// Update user password after OTP verification
    void updatePassword(String mobileNo, String otp, String newPassword);
}