package com.ride_share.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ride_share.entities.Rider;
import com.ride_share.entities.User;
import com.ride_share.playoads.Location;


public interface UserRepo extends JpaRepository<User, Integer>{
	
Optional<User> findByMobileNo(String mobileNo);	
Optional<User> findByEmail(String email);
//List<Rider> findByUser(User user);

@Query("SELECT u.currentLocation FROM User u WHERE u.id = :userId")
Location findLocationByUserId(Integer userId);
}
