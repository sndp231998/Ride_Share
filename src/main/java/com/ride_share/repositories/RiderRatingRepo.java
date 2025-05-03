package com.ride_share.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ride_share.entities.Rider;
import com.ride_share.entities.RiderRating;

public interface RiderRatingRepo extends JpaRepository <RiderRating ,Integer>{

	List<RiderRating> findByRider(Rider rider);

}
