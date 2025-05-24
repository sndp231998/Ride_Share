package com.ride_share.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ride_share.playoads.ApiResponse;
import com.ride_share.playoads.RiderRatingDto;
import com.ride_share.service.RiderRatingService;
import org.springframework.web.bind.annotation.RequestBody;
@RestController
@RequestMapping("/api/v1/rider-ratings")
public class RiderRatingController {

    @Autowired
    private RiderRatingService riderRatingService;

    // ⭐ Create new rating
    @PostMapping("/user/{userId}/rider/{riderId}")
    public ResponseEntity<RiderRatingDto> createRating(
            @RequestBody RiderRatingDto ratingDto,
            @PathVariable Integer userId,
            @PathVariable Integer riderId) {
        
        RiderRatingDto savedRating = riderRatingService.createRiderRating(ratingDto, userId, riderId);
        return new ResponseEntity<>(savedRating, HttpStatus.CREATED);
    }

    // ⭐ Get all ratings of a rider
    @GetMapping("/rider/{riderId}")
    public ResponseEntity<List<RiderRatingDto>> getRatingsByRider(@PathVariable Integer riderId) {
        List<RiderRatingDto> ratings = riderRatingService.getRiderRatingByRider(riderId);
        return new ResponseEntity<>(ratings, HttpStatus.OK);
    }
    
    @GetMapping("/rider/{riderId}/average-rating")
    public ResponseEntity<ApiResponse> getAverageRating(@PathVariable Integer riderId) {
        Double avg = riderRatingService.getAverageRatingByRiderId(riderId);
        return ResponseEntity.ok(new ApiResponse("Average Rating: " + avg, true));
    }



}

