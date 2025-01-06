package com.ride_share.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ride_share.entities.User;


public interface UserRepo extends JpaRepository<User, Integer>{
	
Optional<User> findByMobileNo(String mobileNo);	
Optional<User> findByEmail(String email);
}
