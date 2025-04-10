package com.ride_share.service;

import java.util.Map;

import com.ride_share.playoads.DistanceMatrixResponse;

public interface MapService {

	String getDistanceAndTime(double sourceLat, double sourceLng, double destLat, double destLng);

	int getDistance(double sourceLat, double sourceLng, double destLat, double destLng);

	DistanceMatrixResponse getDistanceMatrixData(double sourceLat, double sourceLng, double destLat, double destLng)
			throws Exception;

	
	//String getCityName(double latitude, double longitude) throws Exception;

}
