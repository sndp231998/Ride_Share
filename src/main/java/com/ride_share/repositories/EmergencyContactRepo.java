package com.ride_share.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ride_share.entities.EmergencyContact;
import com.ride_share.entities.User;


public interface EmergencyContactRepo extends JpaRepository<EmergencyContact,Integer>{
	
	List<EmergencyContact> findByUser(User user);
}
