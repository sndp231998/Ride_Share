package com.bus_ticket.service;

import com.bus_ticket.entities.ForgetPassword;
import com.bus_ticket.playoads.ForgetPasswordDto;

public interface ForgetPasswordService {
	//create for reg
	ForgetPassword createForget(ForgetPasswordDto pas);
	
	// Update user password after OTP verification
    void updatePassword(String mobileNo, String otp, String newPassword);
}