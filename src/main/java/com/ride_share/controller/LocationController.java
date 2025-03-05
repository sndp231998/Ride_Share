package com.ride_share.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import com.ride_share.playoads.LocationDTO;
import com.ride_share.service.LocationService;

@RestController
@RequestMapping("/api/v1/locations")
public class LocationController {

    @Autowired
    private LocationService locationService;

    @PutMapping("/user/{userId}/update")
    public ResponseEntity<String> updateLocation(@PathVariable Integer userId, @RequestBody LocationDTO locationDTO) {
        locationService.updateLocation(userId, locationDTO);
        return ResponseEntity.ok("Location updated successfully");
    }
}

