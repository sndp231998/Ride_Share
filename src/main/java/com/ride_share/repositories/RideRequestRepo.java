package com.ride_share.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ride_share.entities.RideRequest;
import com.ride_share.entities.Rider;
import com.ride_share.entities.User;


public interface RideRequestRepo extends JpaRepository<RideRequest, Integer>{

	List<RideRequest> findByUser(User user);
	List<RideRequest> findByCategory_CategoryId(int categoryId);
}
