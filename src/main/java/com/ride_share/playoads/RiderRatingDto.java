package com.ride_share.playoads;

import lombok.Data;

@Data
public class RiderRatingDto {

	 private int star;
	    private String feedback;
	    private int riderId;
	    private int userId;
}
