package com.ride_share.playoads;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VehicleDto {
    private int id;
    private String vehicleType;
    private String vehicleBrand;
    private String vehicleNumber;
    private String productionYear;
    
	private String vechicleImg; 
	private String billBook1;
	private String billBook2; 
    
	private CategoryDto category; 
	
	
	
	//private UserDto user;
}
