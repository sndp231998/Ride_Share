package com.ride_share.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ride_share.entities.Rider;
import com.ride_share.entities.User;

import com.ride_share.exceptions.ResourceNotFoundException;

import com.ride_share.playoads.RiderDto;

import com.ride_share.repositories.RiderRepo;
import com.ride_share.repositories.UserRepo;
import com.ride_share.service.RiderService;

@Service
public class RiderServiceImpl implements RiderService{

	@Autowired
	private ModelMapper modelMapper;
	@Autowired
	private RiderRepo riderRepo;
	
	@Autowired
	private UserRepo userRepo;
	
	@Override
	public RiderDto createRider(RiderDto riderDto, Integer userId) {
		 User user = this.userRepo.findById(userId)
	                .orElseThrow(() -> new ResourceNotFoundException("User ", "User id", userId));

	     Rider rider = this.modelMapper.map(riderDto, Rider.class);
	     rider.setDriver_License(riderDto.getDriver_License());
	     rider.setDate_Of_Birth(riderDto.getDate_Of_Birth());
	     rider.setSelfieWithIdCard("");
	     rider.setUser(user);
	      
		Rider addrider = this.riderRepo.save(rider);
		return this.modelMapper.map(addrider, RiderDto.class);
	}

	@Override
	public RiderDto updateRider(RiderDto riderDto, Integer riderId) {
		Rider rider = this.riderRepo.findById(riderId)
                .orElseThrow(() -> new ResourceNotFoundException("rider ", "rider id", riderId));

        //Category category = this.categoryRepo.findById(riderDto.getCategory().getCategoryId()).get();

       rider.setDate_Of_Birth(rider.getDate_Of_Birth());
       rider.setDriver_License(riderDto.getDriver_License());
      

        Rider updatedRider = this.riderRepo.save(rider);
        return this.modelMapper.map(updatedRider, RiderDto.class);
	}

	@Override
	public void deleteRider(Integer riderId) {
		 Rider rider = this.riderRepo.findById(riderId)
	                .orElseThrow(() -> new ResourceNotFoundException("Rider ", "rider id", riderId));

	        this.riderRepo.delete(rider);
		
	}

}
