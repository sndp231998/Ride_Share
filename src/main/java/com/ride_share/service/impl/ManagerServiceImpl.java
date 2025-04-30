package com.ride_share.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ride_share.config.AppConstants;
import com.ride_share.entities.Branch;
import com.ride_share.entities.Manager;
import com.ride_share.entities.Role;
import com.ride_share.entities.User;
import com.ride_share.exceptions.ApiException;
import com.ride_share.exceptions.ResourceNotFoundException;

import com.ride_share.playoads.ManagerDto;
import com.ride_share.repositories.BranchRepo;
import com.ride_share.repositories.ManagerRepo;
import com.ride_share.repositories.RoleRepo;
import com.ride_share.repositories.UserRepo;
import com.ride_share.service.ManagerService;

@Service
public class ManagerServiceImpl implements ManagerService {

    @Autowired
    private ManagerRepo managerRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BranchRepo branchRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepo;
    @Override
    public ManagerDto CreateManager(ManagerDto managerDto, Integer branchId) {
        Branch branch = this.branchRepo.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch", "Id", branchId));

        if (managerDto.getMobileNo() == null || managerDto.getMobileNo().isBlank()) {
            throw new ApiException("Mobile number is required to register as manager");
        }
        Manager ma = this.modelMapper.map(managerDto, Manager.class);

        User user = userRepo.findByMobileNo(managerDto.getMobileNo())
        		.orElseThrow(()->new ApiException("Manager Should need to redistred as a user first"));
        
        ma.setUser(user);
        ma.setBranch(branch);
        ma.setManager_wardnumber(managerDto.getManager_wardnumber());
        ma.setManagerDistrict(managerDto.getManagerDistrict());
        ma.setManagerLocalLevel(managerDto.getManagerLocalLevel());
        ma.setManagerProvision(managerDto.getManagerProvision());
        Role role = this.roleRepo.findById(AppConstants.BRANCH_MANAGER_USER).get();
	    user.getRoles().add(role);		   
        Manager savedManager = managerRepo.save(ma);
        return modelMapper.map(savedManager, ManagerDto.class);
    }

    @Override
    public ManagerDto UpdateManager(ManagerDto managerDto, Integer managerId) {
        Manager manager = managerRepo.findById(managerId)
                .orElseThrow(() -> new ResourceNotFoundException("Manager", "Id", managerId));

        manager.setManager_wardnumber(managerDto.getManager_wardnumber());
        manager.setManagerDistrict(managerDto.getManagerDistrict());
        manager.setManagerLocalLevel(managerDto.getManagerLocalLevel());
        manager.setManagerProvision(managerDto.getManagerProvision());

        Manager updated = managerRepo.save(manager);
        return modelMapper.map(updated, ManagerDto.class);
    }

  


    @Override
    public void deleteManager(Integer managerId) {
        Manager manager = managerRepo.findById(managerId)
                .orElseThrow(() -> new ResourceNotFoundException("Manager", "Id", managerId));

        managerRepo.delete(manager);
    }

    @Override
    public List<ManagerDto> getAllManager() {
        List<Manager> managers = managerRepo.findAll();
        return managers.stream()
                .map(manager -> modelMapper.map(manager, ManagerDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public ManagerDto getBranchById(Integer managerId) {
        Manager manager = managerRepo.findById(managerId)
                .orElseThrow(() -> new ResourceNotFoundException("Manager", "Id", managerId));
        return modelMapper.map(manager, ManagerDto.class);
    }

	
}
