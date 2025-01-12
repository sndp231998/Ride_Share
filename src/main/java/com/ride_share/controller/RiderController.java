package com.ride_share.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ride_share.playoads.ApiResponse;
import com.ride_share.playoads.RiderDto;
import com.ride_share.service.FileService;
import com.ride_share.service.RiderService;

@RestController
@RequestMapping("/api/v1/")
public class RiderController {

	@Autowired
	private RiderService riderService;

	@Autowired
	private FileService fileService;

	@Value("${project.image}")
	private String path;
	
	// create Rider--------------------------------------
	 //@PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
	@PostMapping("/user/{userId}/riders")
	public ResponseEntity<RiderDto> createRider(@RequestBody RiderDto riderDto,@PathVariable Integer userId) {
		RiderDto createRiderDto = this.riderService.createRider(riderDto, userId);
		return new ResponseEntity<>(createRiderDto, HttpStatus.CREATED);
	}
	// Update Rider----------------------------------
    @PutMapping("rider/{riderId}")
    public ResponseEntity<RiderDto> updateRider(@RequestBody RiderDto riderDto, 
                                                @PathVariable Integer riderId) {
        RiderDto updatedRider = this.riderService.updateRider(riderDto, riderId);
        return new ResponseEntity<>(updatedRider, HttpStatus.OK);
    }

    // Delete Rider--------------------------
    @DeleteMapping("rider/{riderId}")
    public ResponseEntity<ApiResponse> deleteRider(@PathVariable Integer riderId) {
        this.riderService.deleteRider(riderId);
        return new ResponseEntity<>(new ApiResponse("Rider deleted successfully", true), HttpStatus.OK);
    }
}
