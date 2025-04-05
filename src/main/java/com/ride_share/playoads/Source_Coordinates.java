package com.ride_share.playoads;

import java.time.LocalDateTime;

import javax.persistence.Embeddable;

import lombok.Data;

@Embeddable
@Data
public class Source_Coordinates {

	private double s_latitude;
    private double s_longitude;
    //private LocalDateTime timestamp;
}
