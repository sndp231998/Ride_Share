package com.ride_share.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ride_share.entities.Rider;
import com.ride_share.entities.User;


public interface RiderRepo extends JpaRepository<Rider, Integer>{

	 List<Rider> findByUser(User user);
	 
	 @Query("SELECT r.status FROM Rider r WHERE r.user.id = :userId")
	 Rider.RiderStatus findRiderStatusByUserId(@Param("userId") Integer userId);
}
