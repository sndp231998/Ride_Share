package com.ride_share.service;

import java.util.List;

import com.ride_share.playoads.ManagerDto;

public interface ManagerService {
	
	void deleteManager(Integer managerId);
	
	List<ManagerDto> getAllManager();

	ManagerDto getBranchById(Integer managerId);

	ManagerDto CreateManager(ManagerDto managerDto, Integer branchId);

	ManagerDto UpdateManager(ManagerDto managerDto, Integer managerId);
	
}
