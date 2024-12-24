package com.bus_ticket.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bus_ticket.entities.Role;

public interface RoleRepo extends JpaRepository<Role,Integer> {

}
