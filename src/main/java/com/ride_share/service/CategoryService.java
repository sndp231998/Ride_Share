package com.ride_share.service;

import java.util.List;

import com.ride_share.playoads.CategoryDto;

public interface CategoryService {

	// create
		CategoryDto createCategory(CategoryDto categoryDto);

		// update
		CategoryDto updateCategory(CategoryDto categoryDto, Integer categoryId);

		// delete
		void deleteCategory(Integer categoryId);

		// get
		CategoryDto getCategory(Integer categoryId);

		// get All

		List<CategoryDto> getCategories();
	
}
