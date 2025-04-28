package com.ride_share.service;

import java.util.List;

import com.ride_share.playoads.RideRequestDto;
import com.ride_share.playoads.RiderDto;


public interface RiderService {

	
	//create 

			RiderDto createRider(RiderDto riderDto,Integer userId,Integer categoryId);

			//update 

			RiderDto updateRider(RiderDto riderDto, Integer riderId);

			// delete
			void deleteRider(Integer riderId);

			RiderDto getRiderById(Integer riderId);
			
			List<RiderDto> getRidersByUser(Integer userId);

			List<RiderDto> getAllRiders();

			

			//RiderDto rejectRider(Integer riderId);

			RiderDto approveRider(Integer riderId);

			
			RiderDto addBalanceOfRider(RiderDto riderDto, Integer riderId);

			List<RiderDto> getPendingRiders();

			RiderDto rejectRider(RiderDto riderDto, Integer riderId);


	
}
