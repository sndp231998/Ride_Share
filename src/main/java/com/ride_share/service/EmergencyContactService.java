package com.ride_share.service;

import java.util.List;

import com.ride_share.playoads.EmergencyContactDto;


public interface EmergencyContactService {
EmergencyContactDto CreateEmergencyContact(EmergencyContactDto emergencyContactDto,Integer userId);
	//BranchDto UpdateBranch(BranchDto branchDto, Integer branchId);
	
	void deleteEmergencyContact(Integer econtactId);
	List<EmergencyContactDto> getAllEmergencyContact();
	
	List<EmergencyContactDto> getEmergencyContactsByUser(Integer userId);
	
	
	
}
