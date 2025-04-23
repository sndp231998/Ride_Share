package com.ride_share.playoads;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;

import com.ride_share.entities.Vehicle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {

	private Integer categoryId;
	
	@Column(name="title",length = 10,nullable = false)
	private String categoryTitle;
	
	
	//private List<VehicleDto> vehicles=new ArrayList<>();

	
}
