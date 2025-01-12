package com.ride_share.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ride_share.entities.Rider;


public interface RiderRepo extends JpaRepository<Rider, Integer>{

}
