package com.ride_share.service.impl;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class EmailService {
	 @Autowired
	    private JavaMailSender mailSender;

	 
	 private static final String SMS_API_URL = "https://sms.aakashsms.com/sms/v3/send";
	    private static final String SMS_API_TOKEN = "3b78c6b238c58669f6dbb893261c9e6480fba95361865f0bc143bf03df7ff341";

	    public void sendOtpEmail(String to, String subject, String message) {
	        try {
	            SimpleMailMessage mailMessage = new SimpleMailMessage();
	            mailMessage.setTo(to);
	            mailMessage.setSubject(subject);
	            mailMessage.setText(message);
	            mailMessage.setFrom("info@mytufan.com");

	            mailSender.send(mailMessage);
	            System.out.println("Email sent successfully to " + to);
	        } catch (Exception e) {
	            System.err.println("Failed to send email: " + e.getMessage());
	        }
	    }

	    
	    public void sendOtpMobile(String to, String message) {
		    RestTemplate restTemplate = new RestTemplate();

		    String url = UriComponentsBuilder.fromHttpUrl(SMS_API_URL)
		            .queryParam("auth_token", SMS_API_TOKEN)
		            .queryParam("to", to)
		            .queryParam("text", message)
		            .build(false)  // encoding disabled
		            .toUriString();


		    try {
		        String response = restTemplate.getForObject(url, String.class);
		        System.out.println("SMS sent. Response: " + response);
		    } catch (Exception e) {
		        System.err.println("Failed to send SMS: " + e.getMessage());
		        // You could throw a custom exception if needed
		    }}
	  
	  
	  
	    
	    public String generateOtp() {
	        Random random = new Random();
	        int otp = 100000 + random.nextInt(900000);
	        return String.valueOf(otp);
	    }

	}
