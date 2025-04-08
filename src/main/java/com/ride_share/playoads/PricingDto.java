package com.ride_share.playoads;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.ride_share.entities.Category;

import lombok.Data;

@Data
public class PricingDto {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String province;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private double baseFare;

    @Column(nullable = false)
    private double perKmRate;
    
    @Column(nullable = false)
    private boolean isActive = true;
}
