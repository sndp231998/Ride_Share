package com.ride_share.service;


import java.util.List;

import com.ride_share.playoads.RiderRatingDto;

public interface RiderRatingService {

	RiderRatingDto createRiderRating(RiderRatingDto riderRatingDto,Integer userId,Integer riderId);
	
	
	List<RiderRatingDto> getRiderRatingByRider(Integer riderId);
	public Double getAverageRatingByRiderId(Integer riderId);


}
