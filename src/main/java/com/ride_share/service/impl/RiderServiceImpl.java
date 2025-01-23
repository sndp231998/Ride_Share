package com.ride_share.service.impl;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ride_share.entities.Rider;
import com.ride_share.entities.User;
//import com.ride_share.entities.V;
import com.ride_share.exceptions.ResourceNotFoundException;

import com.ride_share.playoads.RiderDto;
//import com.ride_share.playoads.VDto;
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

		 Rider.RiderStatus existingStatus = this.riderRepo.findRiderStatusByUserId(userId);
		 if (existingStatus == Rider.RiderStatus.PENDING) { 
			 throw new IllegalStateException("Cannot create rider. User already has a rider application in PENDING status."); 
			 }
		 
		    // Validate age based on date of birth
		 int age = calculateAge(riderDto.getDate_Of_Birth());
		 if (age < 18) { 
			 throw new IllegalStateException("Cannot create rider. User must be at least 18 years old.");
			 }
		 
	     Rider rider = this.modelMapper.map(riderDto, Rider.class);
	     rider.setDriver_License(riderDto.getDriver_License());
	     rider.setDate_Of_Birth(riderDto.getDate_Of_Birth());
	     
	     rider.setAddedDate(LocalDateTime.now());
	     rider.setSelfieWithIdCard("");
	     rider.setUser(user);
	     
	     if (existingStatus == null || existingStatus == Rider.RiderStatus.REJECTED) {
	     rider.setStatus(Rider.RiderStatus.PENDING);
	     }
	      
		Rider addrider = this.riderRepo.save(rider);
		return this.modelMapper.map(addrider, RiderDto.class);
	}
	
	private int calculateAge(String dateOfBirth) { 
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate birthDate = LocalDate.parse(dateOfBirth, formatter); 
		return Period.between(birthDate, LocalDate.now()).getYears(); 
		}

	@Override
	public RiderDto updateRider(RiderDto riderDto, Integer riderId) {
	    // Fetch the rider by ID or throw an exception if not found
	    Rider rider = this.riderRepo.findById(riderId)
	            .orElseThrow(() -> new ResourceNotFoundException("Rider", "Rider ID", riderId));

	    // Prevent updates if the status is PENDING
	    if (rider.getStatus() == Rider.RiderStatus.PENDING) {
	        throw new IllegalStateException("Cannot update rider. The application is in PENDING status.");
	    }

	    // Validate age if date of birth is being updated
	    if (riderDto.getDate_Of_Birth() != null) {
	        int age = calculateAge(riderDto.getDate_Of_Birth());
	        if (age < 18) {
	            throw new IllegalStateException("Cannot update rider. User must be at least 18 years old.");
	        }
	        rider.setDate_Of_Birth(riderDto.getDate_Of_Birth());
	    }

	    // Update other fields if they are provided
	    if(rider.getStatus()==Rider.RiderStatus.REJECTED) {
	        rider.setDriver_License(riderDto.getDriver_License());
	    }
	    if (riderDto.getSelfieWithIdCard() != null) {
	        rider.setSelfieWithIdCard(riderDto.getSelfieWithIdCard());
	    }

	    // Set the updated date
	    rider.setUpdatedDate(LocalDateTime.now());

	    // Save the updated rider and return the updated DTO
	    Rider updatedRider = this.riderRepo.save(rider);
	    return this.modelMapper.map(updatedRider, RiderDto.class);
	}

	@Override
	public void deleteRider(Integer riderId) {
		 Rider rider = this.riderRepo.findById(riderId)
	                .orElseThrow(() -> new ResourceNotFoundException("Rider ", "rider id", riderId));

	        this.riderRepo.delete(rider);
		
	}

	@Override
	public RiderDto getRiderById(Integer riderId) {
		Rider rider = this.riderRepo.findById(riderId)
                .orElseThrow(() -> new ResourceNotFoundException("Rider", "rider id", riderId));
        return this.modelMapper.map(rider, RiderDto.class);
	}

	@Override
	public List<RiderDto> getRidersByUser(Integer userId) {
		 User user = this.userRepo.findById(userId)
	                .orElseThrow(() -> new ResourceNotFoundException("User ", "userId ", userId));
	        List<Rider> posts = this.riderRepo.findByUser(user);

	        List<RiderDto> riderDtos = posts.stream().map((rider) -> this.modelMapper.map(rider, RiderDto.class))
	                .collect(Collectors.toList());

	        return riderDtos;
	}

	
	
	@Override
	public List<RiderDto> getAllRiders() {

		List<Rider> riders = this.riderRepo.findAll();
		List<RiderDto> riderDtos = riders.stream().map(rider -> this.riderToDto(rider)).collect(Collectors.toList());

		return riderDtos;
	}
	
	
	public Rider dtoToRider(RiderDto riderDto) {
		Rider rider = this.modelMapper.map(riderDto, Rider.class);

		return rider;
	}

	public RiderDto riderToDto(Rider rider) {
		RiderDto riderDto = this.modelMapper.map(rider, RiderDto.class);
		return riderDto;
	}

}
