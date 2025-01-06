package com.ride_share.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ride_share.entities.OtpRequest;

public interface OtpRequestRepo extends JpaRepository<OtpRequest, Integer>{

	 List<OtpRequest> findByOtp(String otp);
}

