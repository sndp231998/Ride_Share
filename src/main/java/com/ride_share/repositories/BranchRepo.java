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
    @Query("SELECT b FROM Branch b ORDER BY b.branchName ASC, b.branchCode ASC, b.province ASC, b.district ASC")
    List<Branch> findAllSorted();
    
	Optional<User> findById(Branch branch);

}
