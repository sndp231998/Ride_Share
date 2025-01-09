package com.ride_share.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ride_share.entities.Branch;


public interface BranchRepo extends JpaRepository<Branch, Integer> {

	Optional<Branch> findByName(String name);

}
