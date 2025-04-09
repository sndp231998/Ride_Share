package com.ride_share.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ride_share.config.AppConstants;
import com.ride_share.entities.Category;
import com.ride_share.entities.Pricing;
import com.ride_share.entities.User;

import com.ride_share.exceptions.ResourceNotFoundException;
import com.ride_share.playoads.PricingDto;

import com.ride_share.repositories.CategoryRepo;
import com.ride_share.repositories.PricingRepo;
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