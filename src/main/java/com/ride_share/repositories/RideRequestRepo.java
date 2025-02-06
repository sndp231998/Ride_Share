package com.ride_share.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ride_share.entities.RideRequest;


public interface RideRequestRepo extends JpaRepository<RideRequest, Integer>{

}
