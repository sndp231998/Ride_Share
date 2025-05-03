package com.ride_share.playoads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmergencyDTO {
	  private String name;
	    private String phnumber;
	    private String location;
}
