package com.ride_share.playoads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DistanceMatrixResponse {

	 private String originAddress;
	    private String destinationAddress;
	    private String origin;
	    private String destination;
	    private double distance;   // in km
	    private double duration;   // in minutes
	    
}
