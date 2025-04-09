package com.ride_share.service;

import java.util.List;


import com.ride_share.playoads.PricingDto;

public interface PricingService {
   
    void deletePricing(Integer pricingId);
    List<PricingDto> getAllPricing();
   
    PricingDto getPricingById(Integer pricingId);
	PricingDto createPricing(PricingDto pricingDto, Integer userId, Integer categoryId);
	//PricingDto updatePricing(PricingDto pricingDto, Integer pricingId, Integer categoryId);
	PricingDto updatePricing(PricingDto pricingDto, Integer pricingId);
}
