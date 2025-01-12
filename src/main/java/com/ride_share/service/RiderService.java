package com.ride_share.service;

import com.ride_share.playoads.RiderDto;


public interface RiderService {

	
	//create 

			RiderDto createRider(RiderDto riderDto,Integer userId);

			//update 

			RiderDto updateRider(RiderDto riderDto, Integer riderId);

			// delete
			void deleteRider(Integer riderId);
			
	
}
