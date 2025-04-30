package com.ride_share.repositories;

import org.springframework.data.jpa.repository.JpaRepository;


import com.ride_share.entities.Manager;

public interface ManagerRepo extends JpaRepository<Manager, Integer> {

}
