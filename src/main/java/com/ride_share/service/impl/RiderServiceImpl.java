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
	
	// Create a new Rider
    @Override
    public RiderDto createRider(RiderDto riderDto, Integer userId) {
        User user = this.userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "User ID", userId));

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

        // Set status to PENDING only if no prior application or rejected
        rider.setStatus(Rider.RiderStatus.PENDING);

        Rider savedRider = this.riderRepo.save(rider);
        return this.modelMapper.map(savedRider, RiderDto.class);
    }

    // Update an existing Rider
    @Override
    public RiderDto updateRider(RiderDto riderDto, Integer riderId) {
        Rider rider = this.riderRepo.findById(riderId)
                .orElseThrow(() -> new ResourceNotFoundException("Rider", "Rider ID", riderId));

        // Prevent updates if the application is already approved
        if (rider.getStatus() == Rider.RiderStatus.APPROVED) {
            throw new IllegalStateException("Cannot update rider. The application is already APPROVED.");
        }

        // Validate age if date of birth is being updated
        if (riderDto.getDate_Of_Birth() != null) {
            int age = calculateAge(riderDto.getDate_Of_Birth());
            if (age < 18) {
                throw new IllegalStateException("Cannot update rider. User must be at least 18 years old.");
            }
            rider.setDate_Of_Birth(riderDto.getDate_Of_Birth());
        }

        // Update other fields
        rider.setDriver_License(riderDto.getDriver_License());
        rider.setSelfieWithIdCard(riderDto.getSelfieWithIdCard());
        rider.setUpdatedDate(LocalDateTime.now());

        // After update, set status back to PENDING for admin review
        rider.setStatus(Rider.RiderStatus.PENDING);

        Rider updatedRider = this.riderRepo.save(rider);
        return this.modelMapper.map(updatedRider, RiderDto.class);
    }

    // Approve Rider Application
    @Override
    public RiderDto approveRider(Integer riderId) {
        Rider rider = this.riderRepo.findById(riderId)
                .orElseThrow(() -> new ResourceNotFoundException("Rider", "Rider ID", riderId));

        // Only allow approval if status is PENDING
        if (rider.getStatus() != Rider.RiderStatus.PENDING) {
            throw new IllegalStateException("Cannot approve rider. The application is not in PENDING status.");
        }

        rider.setStatus(Rider.RiderStatus.APPROVED);
        rider.setUpdatedDate(LocalDateTime.now());

        Rider approvedRider = this.riderRepo.save(rider);
        return this.modelMapper.map(approvedRider, RiderDto.class);
    }
    
    @Override
    public RiderDto addBalanceOfRider(RiderDto riderDto, Integer riderId) {
        // Find the rider by ID or throw an exception if not found
        Rider rider = this.riderRepo.findById(riderId)
            .orElseThrow(() -> new ResourceNotFoundException("Rider", "Rider ID", riderId));
        
        // Get the current balance and requested balance
        int currentBalance = Integer.parseInt(rider.getBalance());
        int reqBalance = Integer.parseInt(riderDto.getBalance());
        
        // Add the requested balance to the current balance
        int newBalance = currentBalance + reqBalance;
        
        // Update the rider's balance
        rider.setBalance(String.valueOf(newBalance));
        riderRepo.save(rider);
        
        // Update the RiderDto with the new balance
        riderDto.setBalance(String.valueOf(newBalance));
        
        return riderDto;
    }


    // Reject Rider Application
    @Override
    public RiderDto rejectRider(Integer riderId) {
        Rider rider = this.riderRepo.findById(riderId)
                .orElseThrow(() -> new ResourceNotFoundException("Rider", "Rider ID", riderId));

        // Only allow rejection if status is PENDING
        if (rider.getStatus() != Rider.RiderStatus.PENDING) {
            throw new IllegalStateException("Cannot reject rider. The application is not in PENDING status.");
        }

        rider.setStatus(Rider.RiderStatus.REJECTED);
        rider.setUpdatedDate(LocalDateTime.now());

        Rider rejectedRider = this.riderRepo.save(rider);
        return this.modelMapper.map(rejectedRider, RiderDto.class);
    }

    // Helper method to calculate age
    private int calculateAge(String dateOfBirth) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate birthDate = LocalDate.parse(dateOfBirth, formatter);
        return Period.between(birthDate, LocalDate.now()).getYears();
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
