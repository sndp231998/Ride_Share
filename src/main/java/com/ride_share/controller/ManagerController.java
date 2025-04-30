package com.ride_share.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ride_share.playoads.ApiResponse;
import com.ride_share.playoads.ManagerDto;
import com.ride_share.service.ManagerService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/api/v1/managers")
public class ManagerController {

	 @Autowired
	    private ManagerService managerService;

	    @PostMapping("/branch/{branchId}")
	    public ResponseEntity<ManagerDto> createManager(
	            @RequestBody ManagerDto managerDto,
	            @PathVariable Integer branchId
	    ) {
	        ManagerDto createdManager = managerService.CreateManager(managerDto, branchId);
	        return new ResponseEntity<>(createdManager, HttpStatus.CREATED);
	    }
	    
	    // 2. Update Manager
	    @PutMapping("/{managerId}")
	    public ResponseEntity<ManagerDto> updateManager(
	            @RequestBody ManagerDto managerDto,
	            @PathVariable Integer managerId
	    ) {
	        ManagerDto updatedManager = managerService.UpdateManager(managerDto, managerId);
	        return ResponseEntity.ok(updatedManager);
	    }

	    // 3. Delete Manager
	    @DeleteMapping("/{managerId}")
	    public ResponseEntity<ApiResponse> deleteManager(@PathVariable Integer managerId) {
	        managerService.deleteManager(managerId);
	        return ResponseEntity.ok(new ApiResponse("Manager deleted successfully", true));
	    }

	    // 4. Get All Managers
	    @GetMapping("/")
	    public ResponseEntity<List<ManagerDto>> getAllManagers() {
	        List<ManagerDto> managers = managerService.getAllManager();
	        return ResponseEntity.ok(managers);
	    }

	    // 5. Get Single Manager by ID
	    @GetMapping("/{managerId}")
	    public ResponseEntity<ManagerDto> getManagerById(@PathVariable Integer managerId) {
	        ManagerDto manager = managerService.getBranchById(managerId);
	        return ResponseEntity.ok(manager);
	    }
	
}
