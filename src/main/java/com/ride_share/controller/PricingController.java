package com.ride_share.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ride_share.playoads.PricingDto;
import com.ride_share.service.PricingService;

@RestController
@RequestMapping("/api/pricings")
public class PricingController {

    @Autowired
    private PricingService pricingService;

    // 👉 Create Pricing
    @PostMapping("/create")
    public ResponseEntity<PricingDto> createPricing(
            @RequestBody PricingDto pricingDto,
            @RequestParam Integer userId,
            @RequestParam Integer categoryId
    ) {
        PricingDto createdPricing = pricingService.createPricing(pricingDto, userId, categoryId);
        return ResponseEntity.ok(createdPricing);
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
            @PathVariable Integer pricingId,
            @RequestParam Integer categoryId
    ) {
        PricingDto updatedPricing = pricingService.updatePricing(pricingDto, pricingId, categoryId);
        return ResponseEntity.ok(updatedPricing);
    }

    // 👉 Delete Pricing
    @DeleteMapping("/delete/{pricingId}")
    public ResponseEntity<String> deletePricing(@PathVariable Integer pricingId) {
        pricingService.deletePricing(pricingId);
        return ResponseEntity.ok("Pricing deleted successfully.");
    }
}