package com.ride_share.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ride_share.playoads.ApiResponse;
import com.ride_share.playoads.VehicleDto;
import com.ride_share.service.FileService;
import com.ride_share.service.VehicleService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/api/v1/vehicles")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private FileService fileService;

    @Value("${project.image}")
    private String path;

    // Create Vehicle
    @PostMapping("/user/{userId}/category/{categoryId}")
    public ResponseEntity<VehicleDto> createVehicle(@RequestBody VehicleDto vehicleDto, 
                                                    @PathVariable Integer userId, 
                                                    @PathVariable Integer categoryId) {
        VehicleDto createdVehicle = this.vehicleService.createVehicle(vehicleDto, userId, categoryId);
        return new ResponseEntity<>(createdVehicle, HttpStatus.CREATED);
    }

    // Update Vehicle
    @PutMapping("/{vehicleId}")
    public ResponseEntity<VehicleDto> updateVehicle(@RequestBody VehicleDto vehicleDto, 
                                                    @PathVariable Integer vehicleId) {
        VehicleDto updatedVehicle = this.vehicleService.updateVehicle(vehicleDto, vehicleId);
        return new ResponseEntity<>(updatedVehicle, HttpStatus.OK);
    }

    // Delete Vehicle
    @DeleteMapping("/{vehicleId}")
    public ResponseEntity<ApiResponse> deleteVehicle(@PathVariable Integer vehicleId) {
        this.vehicleService.deleteVehicle(vehicleId);
        return new ResponseEntity<>(new ApiResponse("Vehicle deleted successfully", true), HttpStatus.OK);
    }
}
