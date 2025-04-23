package com.ride_share.playoads;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideInfoDto {

	    
	    private double pickupDistance;
	    private double pickupDuration;
	    private double dropDistance;
	    private double dropDuration;
}
