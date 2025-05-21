package com.ride_share.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ride_share.entities.Rider;
import com.ride_share.entities.RiderRating;
import com.ride_share.entities.User;
import com.ride_share.exceptions.ResourceNotFoundException;
import com.ride_share.playoads.RiderDto;
import com.ride_share.playoads.RiderRatingDto;
import com.ride_share.repositories.RiderRatingRepo;
import com.ride_share.repositories.RiderRepo;
import com.ride_share.repositories.UserRepo;
import com.ride_share.service.RiderRatingService;

@Service
public class RiderRatingServiceImpl implements RiderRatingService {

    @Autowired
    private RiderRepo riderRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private RiderRatingRepo riderRatingRepo;

    
    @Override
    public Double getAverageRatingByRiderId(Integer riderId) {
        Rider rider = riderRepo.findById(riderId)
                .orElseThrow(() -> new ResourceNotFoundException("Rider", "Rider ID", riderId));
        
        List<RiderRating> ratings = riderRatingRepo.findByRider(rider);

        if (ratings.isEmpty()) return 0.0;

        double avg = ratings.stream().mapToDouble(RiderRating::getStar).average().orElse(0.0);
        return avg;
    }
    
    @Override
    public RiderRatingDto createRiderRating(RiderRatingDto riderRatingDto, Integer userId, Integer riderId) {
        Rider rider = riderRepo.findById(riderId)
        		.orElseThrow(() -> new ResourceNotFoundException("Rider", "Rider ID", riderId));
        User user = this.userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "User ID", userId));

        RiderRating rating = this.modelMapper.map(riderRatingDto, RiderRating.class);
        
        rating.setRider(rider);
        rating.setUser(user);
        rating.setStar(riderRatingDto.getStar());
        rating.setFeedback(riderRatingDto.getFeedback());
        rating.setRatedAt(LocalDateTime.now());

        RiderRating savedRating = this.riderRatingRepo.save(rating);
        return this.modelMapper.map(savedRating, RiderRatingDto.class);
    }


    @Override
    public List<RiderRatingDto> getRiderRatingByRider(Integer riderId) {

        Rider rider = riderRepo.findById(riderId)
                .orElseThrow(() -> new ResourceNotFoundException("Rider", "Rider ID", riderId));
        
        List<RiderRating> ratings = this.riderRatingRepo.findByRider(rider);

        return ratings.stream()
                .map(rat -> this.modelMapper.map(rat, RiderRatingDto.class))
                .collect(Collectors.toList());
    }
 

}

