package com.ride_share.playoads;



import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.ToString;

@Data
public class DeviceInfoDto {

	private int deviceId;
	private String brand;          // e.g., "Samsung"
    private String device;         // e.g., "SM-G950F"
    private String model;          // e.g., "Galaxy S8"
    private String androidVersion; // e.g., "11"
    private int sdkInt;            // e.g., 30
    @ToString.Exclude
    @JsonIgnore
    private UserDto user;
}
