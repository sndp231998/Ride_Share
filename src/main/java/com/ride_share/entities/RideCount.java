package com.ride_share.entities;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;

@Data
@Entity
public class RideCount {

	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer rideCountId;

	 @ManyToOne
	    @JoinColumn(name = "category_id", nullable = false)
	    private Category category;

	 private Integer totalRide;
	 
	 @ManyToOne
	    @JoinColumn(name = "user_id")//riderId
	    private User user;
	 
	 private LocalDateTime date;
	 
	 
}
