package com.ride_share.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ride_share.service.MapService;

@Service
public class MapServiceImpl implements MapService{

	 private static final String GOOGLE_API_KEY = "YOUR_API_KEY";

	 @Override
	    public String getDistanceAndTime(double sourceLat, double sourceLng, double destLat, double destLng) {
	        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins="
	                + sourceLat + "," + sourceLng +
	                "&destinations=" + destLat + "," + destLng +
	                "&key=" + GOOGLE_API_KEY;

	        // Call API using RestTemplate (Assuming Google API works)
	        RestTemplate restTemplate = new RestTemplate();
	        String response = restTemplate.getForObject(url, String.class);
	        return response;
	    }
}
