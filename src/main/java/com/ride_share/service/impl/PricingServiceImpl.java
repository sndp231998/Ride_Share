package com.ride_share.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;

import org.springframework.stereotype.Service;

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

    private  ModelMapper modelMapper;
    private  PricingRepo pricingRepo;
    private  CategoryRepo categoryRepo;
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
        p.setProvince(pricingDto.getProvince());
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
    public PricingDto updatePricing(PricingDto pricingDto, Integer pricingId, Integer categoryId) {
        Pricing existingPricing = pricingRepo.findById(pricingId)
                .orElseThrow(() -> new ResourceNotFoundException("Pricing", "id", pricingId));

        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        // Update fields
        existingPricing.setProvince(pricingDto.getProvince());
        existingPricing.setCategory(category);
        existingPricing.setBaseFare(pricingDto.getBaseFare());
        existingPricing.setPerKmRate(pricingDto.getPerKmRate());
        existingPricing.setActive(pricingDto.isActive());

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