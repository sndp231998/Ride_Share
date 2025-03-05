package com.ride_share.service;

import com.ride_share.playoads.LocationDTO;

public interface LocationService {

	void updateLocation(Integer userId, LocationDTO locationDTO);
	//LocationDTO getCoordinates(String address);
	 public double[] getCoordinates(String address);
}
