package com.ride_share.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;

@Entity
@Data
public class DeviceInfo {

	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private int deviceId;
	
	private String brand;          // e.g., "Samsung"
    private String device;         // e.g., "SM-G950F"
    private String model;          // e.g., "Galaxy S8"
    private String androidVersion; // e.g., "11"
    private int sdkInt;            // e.g., 30

	@ManyToOne
    @JoinColumn(name = "user_id")// The passenger who created the request pessenger Id
    private User user;
	
	
	
}
