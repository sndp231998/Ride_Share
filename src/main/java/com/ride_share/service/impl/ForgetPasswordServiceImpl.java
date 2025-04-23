package com.ride_share.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ride_share.entities.ForgetPassword;
import com.ride_share.entities.OtpRequest;
import com.ride_share.entities.User;
import com.ride_share.exceptions.ApiException;
import com.ride_share.playoads.ForgetPasswordDto;
import com.ride_share.repositories.ForgetPasswordRepo;
import com.ride_share.repositories.UserRepo;
import com.ride_share.service.ForgetPasswordService;
import com.ride_share.service.OtpRequestService;

@Service
public class ForgetPasswordServiceImpl implements ForgetPasswordService {

    private static final Logger logger = LoggerFactory.getLogger(ForgetPasswordServiceImpl.class);

    @Autowired
    private UserRepo userRepo;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private ForgetPasswordRepo forgetPasswordRepo;
    
    @Autowired
    private OtpRequestService otpRequestService; 

    @Override
    public ForgetPassword createForget(ForgetPasswordDto forgetPasswordDto) {
        String mobileNo = forgetPasswordDto.getPhnum(); // Step 1: Get mobile number from DTO
        logger.debug("Received mobile number: {}", mobileNo);
        
        // Step 2: Check if the user exists based on the mobile number
        Optional<User> userOtp = userRepo.findByMobileNo(mobileNo);
        
        if (userOtp.isPresent()) {
            User user = userOtp.get();  // Extract the user
            String userName = user.getName();  // Get the user's name (assumed to be available)
            logger.debug("User found: {}", userName);
            
            // Step 3: Generate and send OTP with a personalized message
            OtpRequest otpRequest = new OtpRequest();
            otpRequest.setMobileNo(mobileNo);
            String otp = generateOtp(); // Generate OTP
            otpRequest.setOtp(otp); // Set generated OTP
           // otpRequestService.createOtp(otpRequest);  // Save OTP request
            otpRequestService.SendOtpp(otpRequest, mobileNo);
            
//            Dear fname, OTP for your password reset is XXXXXX. Kindly do not share OTP.
//            Thank you for choosing us!
//            Utkrista Shikshya
            String personalizedMessage = String.format(
                "Dear %s, OTP for your password reset is: %s.Kindly do not share otp "
                + "Thank your for choosing us !"
                + "tufan", 
                userName, otp);
            
            otpRequestService.sendMessage(mobileNo, personalizedMessage);
            logger.debug("OTP sent to mobile number: {}", mobileNo);
            
            // Step 4: Check if ForgetPassword entry already exists for this mobile number
            Optional<ForgetPassword> existingForgetPassword = forgetPasswordRepo.findByPhnum(mobileNo);
            
            ForgetPassword forgetPassword;
            // Step 4: Create or update ForgetPassword entry
            if (existingForgetPassword.isPresent()) {
                // If entry exists, update it
                forgetPassword = existingForgetPassword.get();
                forgetPassword.setOtp(otp);  // Update OTP
                forgetPassword.setDate(LocalDateTime.now());  // Update date and time
                logger.debug("Updated existing ForgetPassword entry for mobile number: {}", mobileNo);
            } else {
                // If no entry exists, create a new one
                forgetPassword = new ForgetPassword();
                forgetPassword.setPhnum(mobileNo);
                forgetPassword.setOtp(otp);  // Store the OTP
                forgetPassword.setDate(LocalDateTime.now());  // Set date
                forgetPassword.setUser(user);  // Link user to ForgetPassword entry
                logger.debug("Created new ForgetPassword entry for mobile number: {}", mobileNo);
            }
            
            // Step 5: Save ForgetPassword entity to DB
            return forgetPasswordRepo.save(forgetPassword);
        } else {
            logger.warn("User with the provided mobile number does not exist: {}", mobileNo);
            throw new ApiException("No Account exist with this provided mobile number");
        }
    }
    
    public String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        logger.debug("Generated OTP: {}", otp);
        return String.valueOf(otp);
    }

    @Override
    public void updatePassword(String mobileNo, String otp, String newPassword) {
        // Step 1: Validate input parameters
        if (mobileNo == null || mobileNo.isEmpty()) {
            logger.error("Mobile number cannot be null or empty.");
            throw new ApiException("Mobile number cannot be null or empty.");
        }
        if (otp == null || otp.isEmpty()) {
            logger.error("OTP cannot be null or empty.");
            throw new ApiException("OTP cannot be null or empty.");
        }
        if (newPassword == null || newPassword.isEmpty()) {
            logger.error("New password cannot be null or empty.");
            throw new ApiException("New password cannot be null or empty.");
        }

        logger.debug("Updating password for mobile number: {}", mobileNo);
        
        // Step 2: Find ForgetPassword entry by phone number
        Optional<ForgetPassword> forgetPasswordOpt = forgetPasswordRepo.findByPhnum(mobileNo);

        if (forgetPasswordOpt.isPresent()) {
            ForgetPassword forgetPassword = forgetPasswordOpt.get();

         // Step 3: Check if the OTP is expired (10 minutes limit)
            LocalDateTime otpGenerationTime = forgetPassword.getDate(); // OTP creation time
            LocalDateTime currentTime = LocalDateTime.now(); // Current time

            // Logging OTP generation and current times
            logger.debug("OTP generation time: {}", otpGenerationTime);
            logger.debug("Current time: {}", currentTime);

            // Calculate expiration time (OTP is valid for 10 minutes)
            LocalDateTime otpExpirationTime = otpGenerationTime.plusMinutes(10);

            // Log the expiration time
            logger.debug("OTP will expire at: {}", otpExpirationTime);

            // Step 4: Check if the OTP has expired
            if (currentTime.isAfter(otpExpirationTime)) {
                logger.warn("OTP expired for mobile number: {}", mobileNo);
                throw new ApiException("OTP has expired. Please request a new one.");
            }


            // Step 5: Validate the OTP
            if (forgetPassword.getOtp().equals(otp)) {
                logger.debug("OTP validated for mobile number: {}", mobileNo);

                // Step 6: Fetch the associated User
                Optional<User> userOpt = userRepo.findByMobileNo(mobileNo);
                
                if (userOpt.isPresent()) {
                    User user = userOpt.get();

                    // Step 7: Check if the new password is the same as the old password
                    if (passwordEncoder.matches(newPassword, user.getPassword())) {
                        logger.warn("New password cannot be the same as the old password for mobile number: {}", mobileNo);
                        throw new ApiException("New password cannot be the same as the old password.");
                    }

                    // Step 8: Encode and update the new password
                    String encodedPassword = passwordEncoder.encode(newPassword);
                    user.setPassword(encodedPassword);
                    userRepo.save(user);  // Save the updated user with the new password
                    logger.debug("Password updated successfully for mobile number: {}", mobileNo);

                    // Optionally, delete the ForgetPassword entry
                    // forgetPasswordRepo.delete(forgetPassword);
                } else {
                    logger.warn("User not found for the provided mobile number: {}", mobileNo);
                    throw new ApiException("User not found for the provided mobile number.");
                }
            } else {
                logger.warn("Invalid OTP provided for mobile number: {}", mobileNo);
                throw new ApiException("Invalid OTP. Please try again.");
            }
        } else {
            logger.warn("Forget password request not found for the provided mobile number: {}", mobileNo);
            throw new ApiException("Forget password request not found for the provided mobile number.");
        }
    }



}
