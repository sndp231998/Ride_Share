package com.ride_share.service;

import java.util.List;

import com.ride_share.playoads.BranchDto;

public interface BranchService {

	BranchDto CreateBranch(BranchDto branchDto);
	
	//BranchDto UpdateBranch(BranchDto branchDto, Integer branchId);
	
	void deleteBranch(Integer branchId);
	
	List<BranchDto> getAllBranch();

	BranchDto updateBranch(BranchDto branchDto, Integer branchId);

	BranchDto getBranchById(Integer branchId);
	
}
