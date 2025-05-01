package com.ride_share.entities;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;

import lombok.NoArgsConstructor;


import javax.persistence.*;




@Entity
@Data
@NoArgsConstructor
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String vehicleType;
    private String vehicleBrand;
    private String vehicleNumber;
    private String productionYear;
    private String vehiclecolor;//black
    
	private String vechicleImg; 
	private String billBook1;
	private String billBook2; 
	
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonBackReference
    private Category category;
}
