package com.ride_share.playoads;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyContactDto {
	
	private Integer econtactId;
	
	private String name;
	private String mobile;
	
	   
	    private UserDto user;
	
}
