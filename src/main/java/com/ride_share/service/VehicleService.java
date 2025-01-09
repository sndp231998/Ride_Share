package com.ride_share.service;

import com.ride_share.playoads.VehicleDto;

public interface VehicleService {

	
	//create 

		VehicleDto createVehicle(VehicleDto vehicleDto,Integer userId,Integer categoryId);

		//update 

		VehicleDto updateVehicle(VehicleDto vehicleDto, Integer vehicleId);

		// delete
		void deleteVehicle(Integer vehicleId);
		
}
