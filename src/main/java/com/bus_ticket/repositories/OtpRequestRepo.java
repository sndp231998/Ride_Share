package com.bus_ticket.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bus_ticket.entities.OtpRequest;

public interface OtpRequestRepo extends JpaRepository<OtpRequest, Integer>{

	 List<OtpRequest> findByOtp(String otp);
}

