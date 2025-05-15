package com.ride_share.service.impl;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.ride_share.exceptions.ApiException;
import com.ride_share.playoads.VerificationDto;
import com.ride_share.repositories.UserRepo;


@Service
public class VerificationService {
	@Autowired
	UserRepo userRepo;
	
	@Autowired
	private EmailService emailService;
	

	 private Map<String, VerificationDto> otpStore = new HashMap<>(100, 0.75f);

	 
	 
	 public void sendOtp(String emailOrMobile) {
	        boolean isEmail = emailOrMobile.contains("@");

	        // Validation
	        if ((isEmail && userRepo.findByEmail(emailOrMobile).isPresent()) ||
	        	    (!isEmail && userRepo.findByMobileNo(emailOrMobile).isPresent())) {
	        	    throw new ApiException("User already registered with this " + (isEmail ? "email." : "mobile number."));
	        	}

	        // Generate & store OTP
	        String otp = generateOtp();
	        VerificationDto dto = new VerificationDto();
	        dto.setOtp(otp);
	        dto.setTimestamp(Instant.now());
	        otpStore.put(key(emailOrMobile), dto);

	        // Send
	        String message = "Your OTP is: " + otp;
	        if (isEmail) {
	            emailService.sendOtpEmail(emailOrMobile, "OTP Verification", message);
	        } else {
	            emailService.sendOtpMobile(emailOrMobile, message);
	        }
	    }    
	  
	 public void sendOtpForReset(String emailOrMobile) {
		    // Only check if user exists
		    boolean exists = emailOrMobile.contains("@")
		        ? userRepo.findByEmail(emailOrMobile).isPresent()
		        : userRepo.findByMobileNo(emailOrMobile).isPresent();

		    if (!exists) {
		        throw new ApiException("No user found with this " + (emailOrMobile.contains("@") ? "email." : "mobile number."));
		    }

		    // Send OTP logic same as before
		    String otp = generateOtp();
		    VerificationDto dto = new VerificationDto();
		    dto.setOtp(otp);
		    dto.setTimestamp(Instant.now());
		    otpStore.put(key(emailOrMobile), dto);

		    String message = "Your password reset OTP is: " + otp;
		    if (emailOrMobile.contains("@")) {
		        emailService.sendOtpEmail(emailOrMobile, "Password Reset OTP", message);
		    } else {
		        emailService.sendOtpMobile(emailOrMobile, message);
		    }
		}



	  public boolean verifyOtp(String emailOrMobile, String enteredOtp) {
	        VerificationDto dto = otpStore.get(key(emailOrMobile));
	        if (dto == null || Instant.now().isAfter(dto.getTimestamp().plus(Duration.ofMinutes(5)))) {
	            return false;
	        }
	        return dto.getOtp().equals(enteredOtp);
	    }
	  
	  private String key(String input) {
	        return input.contains("@") ? "email:" + input : "mobile:" + input;
	    }
	  private String generateOtp() {
	        Random random = new Random();
	        int otp = 100000 + random.nextInt(900000);
	        return String.valueOf(otp);
	    }
	  public VerificationDto getOtpDetails(String email) {
		    return otpStore.get(email);
		}
	  public void removeOtp(String email) {
		    otpStore.remove(email);
		}



	  @Scheduled(cron = "0 0 0 * * *") // every day at 12:00 AM
	  public void removeExpiredOtpsAtMidnight() {
	      otpStore.clear();
	      System.out.println("OTP Store cleared at midnight");
	  }

}
