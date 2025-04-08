package com.ride_share.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ride_share.entities.Pricing;

public interface PricingRepo extends JpaRepository<Pricing, Integer> {
    Optional<Pricing> findByProvinceAndCategory_CategoryId(String province, Integer categoryId);
}
