package com.ride_share.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ride_share.entities.Rider;
import com.ride_share.entities.User;
import com.ride_share.playoads.RiderDto.RiderStatus;


public interface RiderRepo extends JpaRepository<Rider, Integer>{

	 List<Rider> findByUser(User user);
	 
	 @Query("SELECT r.status FROM Rider r WHERE r.user.id = :userId")
	 Rider.RiderStatus findRiderStatusByUserId(@Param("userId") Integer userId);
	 
	 List<Rider> findByStatus(Rider.RiderStatus status);

	 Optional<Rider> findByUserId(int userId);
	// Rider findByUserIdAndCategoryId(Integer userId, Integer categoryId);
	 Rider findByUserIdAndCategoryCategoryId(Integer userId, Integer categoryId);

	//List<Rider> findByCategory_CategoryId(int categoryId);

}
