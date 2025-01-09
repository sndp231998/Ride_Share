package com.ride_share.playoads;

import javax.persistence.Column;


import lombok.Data;
import lombok.NoArgsConstructor;
@NoArgsConstructor
@Data
public class CategoryDto {

	private Integer categoryId;
	
	@Column(name="title",length = 10,nullable = false)
	private String categoryTitle;

	
}
