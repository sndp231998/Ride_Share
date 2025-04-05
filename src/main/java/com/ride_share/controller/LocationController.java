//package com.ride_share.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import org.springframework.web.bind.annotation.RestController;
//
//import com.ride_share.playoads.ApiResponse;
//import com.ride_share.playoads.LocationDTO;
//import com.ride_share.service.LocationService;
//@RestController
//@RequestMapping("/api/v1/locations")
//public class LocationController {
//
//    @Autowired
//    private LocationService locationService;
//
//    @PutMapping("/user/{userId}/update/source")
//    public ResponseEntity<ApiResponse> updateSourceLocation(@PathVariable Integer userId, @RequestBody LocationDTO locationDTO) {
//        ApiResponse response = locationService.updateSourceLocation(userId, locationDTO);
//        return ResponseEntity.ok(response);
//    }
//    
//    @PutMapping("/user/{userId}/update/destination")
//    public ResponseEntity<ApiResponse> updateDestinationLocation(@PathVariable Integer userId, @RequestBody LocationDTO locationDTO) {
//        ApiResponse response = locationService.updateDestinationLocation(userId, locationDTO);
//        return ResponseEntity.ok(response);
//    }
//}



