package com.ride_share.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ride_share.entities.Role;

public interface VehicleRepo extends JpaRepository<Role,Integer> {

}
