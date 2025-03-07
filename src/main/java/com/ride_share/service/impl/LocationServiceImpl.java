package com.ride_share.service.impl;

import java.time.LocalDateTime;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ride_share.config.AppConfig;
import com.ride_share.entities.Location;
import com.ride_share.entities.User;
import com.ride_share.exceptions.ResourceNotFoundException;
import com.ride_share.playoads.LocationDTO;
import com.ride_share.repositories.LocationRepo;
import com.ride_share.repositories.UserRepo;
import com.ride_share.service.LocationService;

@Service
public class LocationServiceImpl implements LocationService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private LocationRepo locationRepo;
    
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AppConfig googleMapsConfig;
    
    
    
    @Override
    public double[] getCoordinates(String address) {
        String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=%s",
                address, googleMapsConfig.getGoogleMapsApiKey());

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            try {
                JSONObject jsonResponse = new JSONObject(response.getBody());
                JSONArray results = jsonResponse.getJSONArray("results");

                if (results.length() > 0) {
                    JSONObject location = results.getJSONObject(0)
                                                .getJSONObject("geometry")
                                                .getJSONObject("location");

                    double latitude = location.getDouble("lat");
                    double longitude = location.getDouble("lng");

                    return new double[]{latitude, longitude};  // Returning as an array
                }
            } catch (Exception e) {
                throw new RuntimeException("Error parsing Google Maps API response", e);
            }
        }
        
        throw new RuntimeException("Failed to get coordinates from address");
    }

//continusly location ui bata update garai ranaa
    @Override
    public void updateLocation(Integer userId, LocationDTO locationDTO) {
    	  User user = userRepo.findById(userId)
    	            .orElseThrow(() -> new ResourceNotFoundException("User", "User ID", userId));

        Location location = new Location();
        location.setLatitude(locationDTO.getLatitude());
        location.setLongitude(locationDTO.getLongitude());
        location.setTimestamp(LocalDateTime.now());
        location.setUser(user);

        locationRepo.save(location);
        
        // Update user's current location
        user.setCurrentLocation(location);
        userRepo.save(user);
    }


}
