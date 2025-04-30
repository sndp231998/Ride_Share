package com.ride_share.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ride_share.entities.DeviceInfo;


public interface DeviceInfoRepo extends JpaRepository<DeviceInfo, Integer>{

}
