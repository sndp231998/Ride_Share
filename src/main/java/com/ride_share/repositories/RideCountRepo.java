package com.ride_share.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ride_share.entities.Category;
import com.ride_share.entities.RideCount;
import com.ride_share.entities.User;


public interface RideCountRepo extends JpaRepository<RideCount, Integer> {

	RideCount findByUserAndCategoryAndDate(User user, Category category, LocalDateTime date);
	
	List<RideCount> findByDateBetween(LocalDateTime start, LocalDateTime end);



}
