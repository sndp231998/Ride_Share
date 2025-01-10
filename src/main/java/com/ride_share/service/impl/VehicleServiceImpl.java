package com.ride_share.service.impl;



import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ride_share.entities.Category;
import com.ride_share.entities.User;
import com.ride_share.entities.Vehicle;
import com.ride_share.exceptions.ResourceNotFoundException;
import com.ride_share.playoads.VehicleDto;
import com.ride_share.repositories.CategoryRepo;
import com.ride_share.repositories.UserRepo;
import com.ride_share.repositories.VehicleRepo;
import com.ride_share.service.VehicleService;





@Service
public class VehicleServiceImpl implements VehicleService{

	@Autowired
	private VehicleRepo vehicleRepo;
	   @Autowired
	    private ModelMapper modelMapper;

	    @Autowired
	    private UserRepo userRepo;

	    @Autowired
	    private CategoryRepo categoryRepo;
	
	@Override
	public VehicleDto createVehicle(VehicleDto vehicleDto, Integer userId, Integer categoryId) {
		 User user = this.userRepo.findById(userId)
	                .orElseThrow(() -> new ResourceNotFoundException("User ", "User id", userId));

	        Category category = this.categoryRepo.findById(categoryId)
	                .orElseThrow(() -> new ResourceNotFoundException("Category", "category id ", categoryId));

	        Vehicle vehicle = this.modelMapper.map(vehicleDto, Vehicle.class);
	      
	        vehicle.setVehicle_type(vehicleDto.getVehicle_type());
	        vehicle.setVechicle_Brand(vehicleDto.getVechicle_Brand());
	        vehicle.setVechicle_Number(vehicleDto.getVechicle_Number());
	        vehicle.setProduction_Year(vehicleDto.getProduction_Year());
	        //----image----------
	        vehicle.setVechicle_Img("");
	        vehicle.setBill_book1("");
	        vehicle.setBill_book2("");
	     //---------table-------------
	        vehicle.setCategory(category);
	        vehicle.setUser(user);

	        Vehicle newvehicle = this.vehicleRepo.save(vehicle);

	        return this.modelMapper.map(newvehicle, VehicleDto.class);
	}

	@Override
	public VehicleDto updateVehicle(VehicleDto vehicleDto, Integer vehicleId) {
		Vehicle vehicle = this.vehicleRepo.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Post ", "post id", vehicleId));

        Category category = this.categoryRepo.findById(vehicleDto.getCategory().getCategoryId()).get();

        vehicle.setVehicle_type(vehicleDto.getVehicle_type());
        vehicle.setVechicle_Brand(vehicleDto.getVechicle_Brand());
        vehicle.setVechicle_Number(vehicle.getVechicle_Number());
        vehicle.setProduction_Year(vehicleDto.getProduction_Year());
      
        vehicle.setCategory(category);


        Vehicle updatedvehicle = this.vehicleRepo.save(vehicle);
        return this.modelMapper.map(updatedvehicle, VehicleDto.class);
	}

	@Override
	public void deleteVehicle(Integer vehicleId) {
		  Vehicle vehicle = this.vehicleRepo.findById(vehicleId)
	                .orElseThrow(() -> new ResourceNotFoundException("Vehicle ", "vehicle id", vehicleId));

	        this.vehicleRepo.delete(vehicle);

		
	}

	
}
