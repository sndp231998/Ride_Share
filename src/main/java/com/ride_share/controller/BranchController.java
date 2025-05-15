package com.ride_share.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ride_share.playoads.ApiResponse;
import com.ride_share.playoads.BranchDto;
import com.ride_share.service.BranchService;

@RestController
@RequestMapping("/api/v1/branches")
public class BranchController {

    @Autowired
    private BranchService branchService;

    @PostMapping("/")
    public ResponseEntity<BranchDto> createBranch(@RequestBody BranchDto branchDto) {
        BranchDto createdBranch = this.branchService.CreateBranch(branchDto);
        return new ResponseEntity<>(createdBranch, HttpStatus.CREATED);
    }

    @PutMapping("/{branchId}")
    public ResponseEntity<BranchDto> updateBranch(@RequestBody BranchDto branchDto, @PathVariable Integer branchId) {
        BranchDto updatedBranch = this.branchService.updateBranch(branchDto, branchId);
        return new ResponseEntity<>(updatedBranch, HttpStatus.OK);
    }
    @DeleteMapping("/{branchId}")
    public ResponseEntity<ApiResponse> deleteBranch(@PathVariable Integer branchId) {
        this.branchService.deleteBranch(branchId);
        ApiResponse response = new ApiResponse("Branch deleted successfully", true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("/")
    public ResponseEntity<List<BranchDto>> getAllBranches() {
        return ResponseEntity.ok(this.branchService.getAllBranch());
    }

    @GetMapping("/{branchId}")
    public ResponseEntity<BranchDto> getBranchById(@PathVariable Integer branchId) {
        BranchDto branch = this.branchService.getBranchById(branchId);
        return new ResponseEntity<>(branch, HttpStatus.OK);
    }
}
