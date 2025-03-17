package com.ride_share.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ride_share.entities.Branch;
import com.ride_share.entities.User;


public interface BranchRepo extends JpaRepository<Branch, Integer> {

	Optional<Branch> findByName(String name);
	// Custom query to find and sort by specific fields
    
    
	Optional<User> findById(Branch branch);

}
