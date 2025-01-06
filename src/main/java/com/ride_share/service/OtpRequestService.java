package com.ride_share.service;

import com.ride_share.entities.OtpRequest;

public interface OtpRequestService {
	 //create for reg
	OtpRequest createOtp(OtpRequest otpReq);
	
	 // Send OTP for reg
    OtpRequest SendOtp(OtpRequest otpReq, String phnumber);

	void sendMessage(String mobileNo,String message);
	
	 void sendOtpSms(String mobileNo,String otp);

	 //for forget password
	OtpRequest SendOtpp(OtpRequest otpReq, String phnumber);
}