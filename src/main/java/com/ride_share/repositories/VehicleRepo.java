package com.ride_share.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ride_share.entities.Category;
import com.ride_share.entities.User;
import com.ride_share.entities.Vehicle;

public interface VehicleRepo extends JpaRepository<Vehicle, Integer> {

	List<Vehicle> findByCategory(Category cat);

	List<Vehicle> findByUser(User user);

	
	@Query("SELECT v FROM Vehicle v WHERE " + 
	"LOWER(v.vehicleNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
			"LOWER(v.productionYear) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " + 
	"LOWER(v.vehicleType) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " + 
			"LOWER(v.user.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
	"v.user.mobileNo LIKE CONCAT('%', :keyword, '%') OR " + 
	"LOWER(v.user.branch_Name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
	List<Vehicle> searchVehicles(@Param("keyword") String keyword);
}
