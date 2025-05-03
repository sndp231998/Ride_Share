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
public class RiderRating {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long riderRatingId;

    private int star; // 1 to 5 star
    private String feedback; // Optional text like "Good Rider", "Late", etc.

    @ManyToOne
    @JoinColumn(name = "rider_id")
    private Rider rider;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // who gave the rating

    private LocalDateTime ratedAt ;
}
