package com.ride_share.service;

public interface MapService {

	String getDistanceAndTime(double sourceLat, double sourceLng, double destLat, double destLng);

	//String getCityName(double latitude, double longitude) throws Exception;

}
