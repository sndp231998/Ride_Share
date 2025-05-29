package com.ride_share.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.ride_share.config.AppConstants;
import com.ride_share.entities.Category;
import com.ride_share.entities.Pricing;
import com.ride_share.entities.RideCount;
import com.ride_share.entities.Rider;
import com.ride_share.entities.RiderTransaction;
import com.ride_share.entities.User;

import com.ride_share.exceptions.ResourceNotFoundException;
import com.ride_share.playoads.PricingDto;

import com.ride_share.repositories.CategoryRepo;
import com.ride_share.repositories.PricingRepo;
import com.ride_share.repositories.RideCountRepo;
import com.ride_share.repositories.RiderRepo;
import com.ride_share.repositories.RiderTransactionRepo;
import com.ride_share.repositories.UserRepo;
import com.ride_share.service.PricingService;



@Service
public class PricingServiceImpl implements PricingService {
    @Autowired
    private  ModelMapper modelMapper;
    @Autowired
    private  PricingRepo pricingRepo;
    @Autowired
    private  CategoryRepo categoryRepo;
    @Autowired
    private  UserRepo userRepo;
    
    @Autowired
    private RideCountRepo rideCountRepo;
    
    @Autowired
    private RiderRepo riderRepo;
    
    @Autowired
    private EmailService emailService;

    @Autowired
    private RiderTransactionRepo riderTransactionRepo;
    
    
    
    
    
    
    // @Transactional
     @Scheduled(cron = "57 23 * * *") // Runs daily at 11:57 PM
     public void deductBalanceBasedOnRides() {
         LocalDate today = LocalDate.now();
         StringBuilder logMessage = new StringBuilder();

         try {
             List<RideCount> todayRideCounts = rideCountRepo.findByDateBetween(
                 today.atStartOfDay(), today.plusDays(1).atStartOfDay()
             );

             Map<String, Long> rideSummary = todayRideCounts.stream()
                 .collect(Collectors.groupingBy(
                     rc -> String.valueOf(rc.getUser().getId()) + "-" + String.valueOf(rc.getCategory().getCategoryId()),
                     Collectors.summingLong(RideCount::getTotalRide)
                 ));

             for (Map.Entry<String, Long> entry : rideSummary.entrySet()) {
                 try {
                     String[] parts = entry.getKey().split("-");
                     Integer userId = Integer.parseInt(parts[0]);
                     Integer categoryId = Integer.parseInt(parts[1]);
                     Long totalRides = entry.getValue();

                     Rider rider = riderRepo.findByUserIdAndCategoryCategoryId(userId, categoryId);
                     if (rider != null) {
                         double deductAmount = 0;

                         if (categoryId == 1) {
                        	    if (totalRides <= 5) deductAmount = 50;
                        	    else deductAmount = 100;
                        	} else if (categoryId == 2) {
                        	    if (totalRides <= 5) deductAmount = 80;
                        	    else deductAmount = 150;
                        	}

                         if (deductAmount > 0 && rider.getBalance() != null) {
                             if (rider.getBalance() >= deductAmount) {
                                 double newBalance = rider.getBalance() - deductAmount;
                                 rider.setBalance(newBalance);
                                 riderRepo.save(rider);
                                 
                                 
                                 RiderTransaction txn = new RiderTransaction();
                                 txn.setRider(rider);
                                 txn.setAmount(-deductAmount); // Negative means debit
                                 txn.setType("DEBIT");
                                 txn.setReason("Ride Deduction for category " + categoryId);
                                 txn.setDateTime(LocalDateTime.now());
                                 riderTransactionRepo.save(txn); // Save transaction
                                 
                                 
                                 logMessage.append("Deducted Rs. ").append(deductAmount)
                                     .append(" from RiderID ").append(rider.getId()).append("\n");
                             } else {
                                 logMessage.append("Skipped deduction for RiderID ").append(rider.getId())
                                     .append(" due to insufficient balance.\n");
                             }
                         }
                     }
                 } catch (Exception e) {
                     logMessage.append("Error processing entry ").append(entry.getKey()).append(": ").append(e.getMessage()).append("\n");
                 }
             }

             // ✅ Message on successful run
             emailService.sendOtpMobile("9816032025", "✅ Balance deduction task completed.\n" + logMessage.toString());

         } catch (Exception ex) {
             // ❌ Message on error in entire method
             emailService.sendOtpMobile("9816032025", "❌ Error in balance deduction method: " + ex.getMessage());
         }
     }
    
    
    
    
    
    //Integer userId, Integer categoryId
    @Override
    public PricingDto createPricing(PricingDto pricingDto,Integer userId,Integer categoryId) {
    	 // Fetch User
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
        
        // Fetch Category
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        Pricing p = modelMapper.map(pricingDto, Pricing.class);
        p.setBaseFare(pricingDto.getBaseFare());
        p.setPerKmRate(pricingDto.getPerKmRate());
        //p.setProvince(pricingDto.getProvince());
        p.setUser(user);
     // Normalize and Validate Province
        String userProvince = pricingDto.getProvince().trim().toLowerCase();
        Optional<String> validProvince = AppConstants.VALID_PROVINCES.stream()
                .filter(province -> province.toLowerCase().startsWith(userProvince))
                .findFirst();

        if (validProvince.isEmpty()) {
            throw new IllegalArgumentException("Invalid province: " + userProvince);
        }

        p.setProvince(validProvince.get());  // Save the valid province
        p.setCategory(category);
        Pricing savedp = pricingRepo.save(p);
        return modelMapper.map(savedp, PricingDto.class);
        }

       

    @Override
    public void deletePricing(Integer pricingId) {
        Pricing pricing = pricingRepo.findById(pricingId)
                .orElseThrow(() -> new ResourceNotFoundException("Pricing", "id", pricingId));
        pricingRepo.delete(pricing);
    }

    @Override
    public List<PricingDto> getAllPricing() {
        List<Pricing> pricings = pricingRepo.findAll();
        return pricings.stream()
                .map(pricing -> modelMapper.map(pricing, PricingDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public PricingDto updatePricing(PricingDto pricingDto, Integer pricingId) {
        Pricing existingPricing = pricingRepo.findById(pricingId)
                .orElseThrow(() -> new ResourceNotFoundException("Pricing", "id", pricingId));

        Category category = categoryRepo.findById(pricingDto.getCategory().getCategoryId())
				.orElseThrow(() -> new ResourceNotFoundException("Category", "category id", pricingDto.getCategory().getCategoryId()));
	
        // Normalize and Validate Province
        String userProvince = pricingDto.getProvince().trim().toLowerCase();
        Optional<String> validProvince = AppConstants.VALID_PROVINCES.stream()
                .filter(province -> province.toLowerCase().startsWith(userProvince))
                .findFirst();

        if (validProvince.isEmpty()) {
            throw new IllegalArgumentException("Invalid province: " + userProvince);
        }

        // Update fields
        existingPricing.setProvince(validProvince.get());  // Save the standardized province
        existingPricing.setCategory(category);
        existingPricing.setBaseFare(pricingDto.getBaseFare());
        existingPricing.setPerKmRate(pricingDto.getPerKmRate());

     // Handle isActive attribute (default value is true)
        Boolean isActive = pricingDto.isActive(); // ✅ must use getIsActive() for Boolean
        existingPricing.setActive(isActive != null ? isActive : true); // default true if null



        Pricing updatedPricing = pricingRepo.save(existingPricing);
        return modelMapper.map(updatedPricing, PricingDto.class);
    }


    @Override
    public PricingDto getPricingById(Integer pricingId) {
        Pricing pricing = pricingRepo.findById(pricingId)
                .orElseThrow(() -> new ResourceNotFoundException("Pricing", "id", pricingId));
        return modelMapper.map(pricing, PricingDto.class);
    }

   

	
}