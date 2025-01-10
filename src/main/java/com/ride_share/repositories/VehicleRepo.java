package com.ride_share.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ride_share.entities.Vehicle;

public interface VehicleRepo extends JpaRepository<Vehicle,Integer> {

}
