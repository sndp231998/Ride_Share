package com.ride_share.service.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Random;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ride_share.entities.OtpRequest;
import com.ride_share.entities.User;
import com.ride_share.repositories.OtpRequestRepo;
import com.ride_share.repositories.UserRepo;
import com.ride_share.service.OtpRequestService;


@Service
public class OtpRequestServiceImpl implements OtpRequestService {
    private static final Logger logger = LoggerFactory.getLogger(OtpRequestServiceImpl.class);

  

    @Autowired
    private OtpRequestRepo otpRequestRepo;

    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private UserRepo userRepo;  

    private static final String SMS_API_URL = "https://sms.aakashsms.com/sms/v3/send";
    private static final String SMS_API_TOKEN = "3b78c6b238c58669f6dbb893261c9e6480fba95361865f0bc143bf03df7ff341";

    public String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
  
    public void sendOtpSms(String mobileNo, String otp) {
        try {
            String url = String.format("%s?auth_token=%s&to=%s&text=Dear valuable Customer, OTP for Tuffan is : %s Thank you for choosing us! Tufan",
                    SMS_API_URL, SMS_API_TOKEN, mobileNo, otp);

            logger.info("Sending OTP to mobile number: {}", mobileNo);
            logger.info("Generated OTP: {}", otp);
            logger.info("Final URL: {}", url);

            String response = restTemplate.getForObject(url, String.class);
            logger.info("Response from SMS API: {}", response);
        } catch (Exception e) {
            logger.error("Failed to send OTP to {}", mobileNo, e);
            throw new RuntimeException("OTP pathauna milena, kripaya feri kosis garnuhos!");
        }
    }

    
    
    @Override
    public void sendMessage(String mobileNo, String message) {
        try {
            String url = String.format("%s?auth_token=%s&to=%s&text=%s",
                    SMS_API_URL, SMS_API_TOKEN, mobileNo, message);

            logger.info("Sending message to mobile number: {}", mobileNo);
            logger.info("Message: {}", message);
            logger.info("Final URL: {}", url);

            String response = restTemplate.getForObject(url, String.class);
            logger.info("Response from SMS API: {}", response);
        } catch (Exception e) {
            logger.error("Failed to send message to {}", mobileNo, e);
            throw new RuntimeException("SMS pathauna samasya aayo!");
        }
    }

    
    
    @Override
    public OtpRequest createOtp(OtpRequest otpReq) {
        if (otpReq.getMobileNo() == null || otpReq.getMobileNo().isEmpty()){
            throw new IllegalArgumentException("Phone number is required");
        }
        SendOtp(otpReq, otpReq.getMobileNo());
        return otpRequestRepo.save(otpReq);
    }

    @Override
    public OtpRequest SendOtp(OtpRequest otpReq, String phnumber) {
    	Optional<User> mobileExists = userRepo.findByMobileNo(phnumber);
    	
    	if (mobileExists.isPresent()) {
            logger.warn("Mobile number {} already exists in the system. OTP will not be sent.", phnumber);
            throw new IllegalArgumentException("Mobile number already registered.");
        }
        String ph = otpReq.getMobileNo();
        String otp = generateOtp();
        otpReq.setOtp(otp);
        sendOtpSms(ph, otp);
        Instant otpValidUntilInstant = Instant.now().plus(10, ChronoUnit.MINUTES);
        LocalDateTime otpValidUntil = LocalDateTime.ofInstant(otpValidUntilInstant, ZoneId.systemDefault());
        otpReq.setOtpValidUntil(otpValidUntil); // OTP valid for 10 minutes
        
        return otpRequestRepo.save(otpReq);
    }

@Override
    public OtpRequest SendOtpp(OtpRequest otpReq, String phnumber) {
    	Optional<User> mobileExists = userRepo.findByMobileNo(phnumber);
    	if (mobileExists.isPresent()) {
           
        
        String ph = otpReq.getMobileNo();
        String otp = generateOtp();
        otpReq.setOtp(otp);
        sendOtpSms(ph, otp);
        Instant otpValidUntilInstant = Instant.now().plus(10, ChronoUnit.MINUTES);
        LocalDateTime otpValidUntil = LocalDateTime.ofInstant(otpValidUntilInstant, ZoneId.systemDefault());
        otpReq.setOtpValidUntil(otpValidUntil); // OTP valid for 10 minutes
    	}
        return otpRequestRepo.save(otpReq);
    }
	

}
