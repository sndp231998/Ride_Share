package com.ride_share.playoads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DistanceMatrixResponse {
	  private String originAddress;      // source name
	    private String destinationAddress; // destination name
	    private double distanceKm;         // in kilometers (e.g. 8.6)
	    private double durationMin;        // in minutes (e.g. 23.0)
}



