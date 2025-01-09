package com.ride_share.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ride_share.entities.Category;


public interface CategoryRepo extends JpaRepository<Category, Integer> {

}
