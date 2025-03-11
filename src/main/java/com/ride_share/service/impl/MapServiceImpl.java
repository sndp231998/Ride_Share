package com.ride_share.service.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;
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
	    };
	    

	    public String getCityName(double latitude, double longitude) throws Exception {
	        String urlStr = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude + "&key=" + GOOGLE_API_KEY;
	        URL url = new URL(urlStr);
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setRequestMethod("GET");
	        
	        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	        String inputLine;
	        StringBuilder response = new StringBuilder();
	        
	        while ((inputLine = in.readLine()) != null) {
	            response.append(inputLine);
	        }
	        in.close();
	        
	        JSONObject jsonResponse = new JSONObject(response.toString());
	        if ("OK".equals(jsonResponse.getString("status"))) {
	            JSONArray results = jsonResponse.getJSONArray("results");
	            if (results.length() > 0) {
	                JSONObject addressComponents = results.getJSONObject(0);
	                JSONArray components = addressComponents.getJSONArray("address_components");
	                for (int i = 0; i < components.length(); i++) {
	                    JSONObject component = components.getJSONObject(i);
	                    JSONArray types = component.getJSONArray("types");
	                    for (int j = 0; j < types.length(); j++) {
	                        if ("locality".equals(types.getString(j))) {
	                            return component.getString("long_name");
	                        }
	                    }
	                }
	            }
	        }
	        throw new Exception("City not found");
	    }
}
