package com.bus_ticket.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bus_ticket.entities.Role;

public interface RoleRepo extends JpaRepository<Role,Integer> {
	Optional<Role> findByName(String name);
}
