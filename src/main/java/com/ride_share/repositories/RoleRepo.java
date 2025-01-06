package com.ride_share.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ride_share.entities.Role;

public interface RoleRepo extends JpaRepository<Role,Integer> {
	Optional<Role> findByName(String name);
}
