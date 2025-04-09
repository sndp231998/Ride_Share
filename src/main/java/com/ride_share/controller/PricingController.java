package com.ride_share.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import com.ride_share.playoads.PricingDto;

import com.ride_share.service.PricingService;

@RestController
@RequestMapping("/api/v1/pricings")
public class PricingController {

    @Autowired
    private PricingService pricingService;

    // 👉 Create Pricing
    @PostMapping("/user/{userId}/category/{categoryId}")
    public ResponseEntity<PricingDto> createdPricing(
            @RequestBody PricingDto pricingDto,
            @PathVariable Integer userId,
            @PathVariable Integer categoryId
    ) {
        PricingDto createdPricing = pricingService.createPricing(pricingDto, userId, categoryId);
        return new ResponseEntity<>(createdPricing,HttpStatus.CREATED);
    }

  
    
    
    // 👉 Get All Pricings
    @GetMapping("/")
    public ResponseEntity<List<PricingDto>> getAllPricings() {
        return ResponseEntity.ok(pricingService.getAllPricing());
    }

    // 👉 Get Pricing by ID
    @GetMapping("/{pricingId}")
    public ResponseEntity<PricingDto> getPricingById(@PathVariable Integer pricingId) {
        return ResponseEntity.ok(pricingService.getPricingById(pricingId));
    }

    // 👉 Update Pricing
    @PutMapping("/update/{pricingId}")
    public ResponseEntity<PricingDto> updatePricing(
            @RequestBody PricingDto pricingDto,
            @PathVariable Integer pricingId
            
    ) {
        PricingDto updatedPricing = pricingService.updatePricing(pricingDto, pricingId);
        return ResponseEntity.ok(updatedPricing);
    }

    // 👉 Delete Pricing
    @DeleteMapping("/delete/{pricingId}")
    public ResponseEntity<String> deletePricing(@PathVariable Integer pricingId) {
        pricingService.deletePricing(pricingId);
        return ResponseEntity.ok("Pricing deleted successfully.");
    }
}