package com.ride_share.service;

import java.util.List;

import com.ride_share.playoads.VehicleDto;

public interface VehicleService {

	VehicleDto createVehicle(VehicleDto vehicleDto,Integer userId,Integer categoryId);
	
	VehicleDto updateVehicle(VehicleDto vehicleDto, Integer vehicleId);
	
	
	
//	// delete
	void deleteVehicle(Integer vehicleId);

	VehicleDto getVehicleById(Integer vehicleId);

	List<VehicleDto> getAllVehicles();

	List<VehicleDto> getVehiclesByCategory(Integer categoryId);

	List<VehicleDto> getVehiclesByUser(Integer userId);

	List<VehicleDto> searchVehicles(String keyword);
	
}
