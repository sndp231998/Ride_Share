package com.ride_share.service.impl;

import java.util.List;
import java.util.stream.Collectors;

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
public class VehicleServiceImpl implements VehicleService {

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
        // Fetch User
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
        
        // Fetch Category
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        Vehicle vehicle = modelMapper.map(vehicleDto, Vehicle.class);
        vehicle.setBillBook1("");
        vehicle.setBillBook2("");
        vehicle.setVechicleImg("");
        vehicle.setUser(user);
     
        vehicle.setCategory(category);
        
        Vehicle savedVehicle = vehicleRepo.save(vehicle);
        return modelMapper.map(savedVehicle, VehicleDto.class);
    }

	@Override
	public VehicleDto updateVehicle(VehicleDto vehicleDto, Integer vehicleId) {
		Vehicle vehicle = vehicleRepo.findById(vehicleId) 
				.orElseThrow(() -> new ResourceNotFoundException("Vehicle", "vehicle id", vehicleId));
		Category category = categoryRepo.findById(vehicleDto.getCategory().getCategoryId())
				.orElseThrow(() -> new ResourceNotFoundException("Category", "category id", vehicleDto.getCategory().getCategoryId()));
	
	
		vehicle.setVehicleType(vehicleDto.getVehicleType()); 
		vehicle.setVehicleBrand(vehicleDto.getVehicleBrand()); 
		vehicle.setVehicleNumber(vehicleDto.getVehicleNumber());
		vehicle.setProductionYear(vehicleDto.getProductionYear());
		vehicle.setBillBook1(vehicleDto.getBillBook1());
		vehicle.setBillBook2(vehicleDto.getBillBook2());
		vehicle.setVechicleImg(vehicleDto.getVechicleImg());
		vehicle.setCategory(category);
		Vehicle updatedVehicle = vehicleRepo.save(vehicle); 
		return modelMapper.map(updatedVehicle, VehicleDto.class);
	}

	@Override
	public void deleteVehicle(Integer vehicleId) {
		  Vehicle vehicle = this.vehicleRepo.findById(vehicleId)
	                .orElseThrow(() -> new ResourceNotFoundException("Vehicle ", "vehicle id", vehicleId));

	        this.vehicleRepo.delete(vehicle);

		
	}

	@Override
	public VehicleDto getVehicleById(Integer vehicleId) {
		Vehicle vehicle = this.vehicleRepo.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "vehicle id", vehicleId));
        return this.modelMapper.map(vehicle, VehicleDto.class);
	}

	
	
	  @Override
	    public List<VehicleDto> getVehiclesByCategory(Integer categoryId) {

	        Category cat = this.categoryRepo.findById(categoryId)
	                .orElseThrow(() -> new ResourceNotFoundException("Category", "category id", categoryId));
	        List<Vehicle> vehicles = this.vehicleRepo.findByCategory(cat);

	        List<VehicleDto> vehicleDtos = vehicles.stream().map((vehicle) -> this.modelMapper.map(vehicle, VehicleDto.class))
	                .collect(Collectors.toList());

	        return vehicleDtos;
	    }
//
//	
//	//---get all vehicles
//	
	@Override
	public List<VehicleDto> getAllVehicles() {

		List<Vehicle> vehicles = this.vehicleRepo.findAll();
		List<VehicleDto> vehicleDtos = vehicles.stream().map(vehicle -> this.vehicleToDto(vehicle)).collect(Collectors.toList());

		return vehicleDtos;
	}

	public Vehicle dtoToVehicle(VehicleDto vehicleDto) {
	Vehicle vehicle = this.modelMapper.map(vehicleDto, Vehicle.class);

	return vehicle;
}

public VehicleDto vehicleToDto(Vehicle vehicle) {
	VehicleDto vehicleDto = this.modelMapper.map(vehicle, VehicleDto.class);
	return vehicleDto;
}

@Override
public List<VehicleDto> getVehiclesByUser(Integer userId) {
	 User user = this.userRepo.findById(userId)
             .orElseThrow(() -> new ResourceNotFoundException("User ", "userId ", userId));
     List<Vehicle> vehicles = this.vehicleRepo.findByUser(user);

     List<VehicleDto> vehicleDtos = vehicles.stream().map((vehicle) -> this.modelMapper.map(vehicle, VehicleDto.class))
             .collect(Collectors.toList());

     return vehicleDtos;
}

@Override
public List<VehicleDto> searchVehicles(String keyword) {
	List<Vehicle> vehicles = vehicleRepo.searchVehicles(keyword);
	return vehicles.stream() .map(vehicle -> modelMapper.map(vehicle, VehicleDto.class)) 
			.collect(Collectors.toList()); }

}
