package com.ride_share.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ride_share.entities.Category;
import com.ride_share.exceptions.ResourceNotFoundException;
import com.ride_share.playoads.CategoryDto;
import com.ride_share.repositories.CategoryRepo;
import com.ride_share.service.CategoryService;



@Service
public class CategoryServiceImpl implements CategoryService{
	@Autowired
	private CategoryRepo categoryRepo;

	@Autowired
	private ModelMapper modelMapper;

	@Override
	public CategoryDto createCategory(CategoryDto categoryDto) {
		Category cat = this.modelMapper.map(categoryDto, Category.class);
		
		Category addedCat = this.categoryRepo.save(cat);
		return this.modelMapper.map(addedCat, CategoryDto.class);
	}

	@Override
	public CategoryDto updateCategory(CategoryDto categoryDto, Integer categoryId) {

		Category cat = this.categoryRepo.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category ", "Category Id", categoryId));

		cat.setCategoryTitle(categoryDto.getCategoryTitle());
		
		Category updatedcat = this.categoryRepo.save(cat);

		return this.modelMapper.map(updatedcat, CategoryDto.class);
	}

	//Delete Category
		@Override
		public void deleteCategory(Integer categoryId) {

			Category cat = this.categoryRepo.findById(categoryId)
					.orElseThrow(() -> new ResourceNotFoundException("Category ", "category id", categoryId));
			this.categoryRepo.delete(cat);
		}

		
		//GetCAtegory BY Id
		@Override
		public CategoryDto getCategory(Integer categoryId) {
			Category cat = this.categoryRepo.findById(categoryId)
					.orElseThrow(() -> new ResourceNotFoundException("Category", "category id", categoryId));

			return this.modelMapper.map(cat, CategoryDto.class);
		}

		//GetAll CAtegory
		@Override
		public List<CategoryDto> getCategories() {

			List<Category> categories = this.categoryRepo.findAll();
			List<CategoryDto> catDtos = categories.stream().map((cat) -> this.modelMapper.map(cat, CategoryDto.class))
					.collect(Collectors.toList());

			return catDtos;
		}
}