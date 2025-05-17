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
import com.ride_share.playoads.NotificationDto;
import com.ride_share.repositories.BranchRepo;
import com.ride_share.repositories.ManagerRepo;
import com.ride_share.repositories.RoleRepo;
import com.ride_share.repositories.UserRepo;
import com.ride_share.service.ManagerService;
import com.ride_share.service.NotificationService;

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
    EmailService  emailService;

    @Autowired
    private RoleRepo roleRepo;
    
    @Autowired
	 NotificationService  notificationService;
    @Override
    public ManagerDto CreateManager(ManagerDto managerDto, Integer branchId) {
        Branch branch = this.branchRepo.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch", "Id", branchId));

        if (managerDto.getMobileNo() == null || managerDto.getMobileNo().isBlank()) {
            throw new ApiException("Mobile number required of manager");
        }
        Manager ma = this.modelMapper.map(managerDto, Manager.class);

        User user = userRepo.findByMobileNo(managerDto.getMobileNo())
        		.orElseThrow(()->new ApiException("Manager Should need to registred as a user first"));
        
        ma.setUser(user);
        ma.setBranch(branch);
        ma.setManager_wardnumber(managerDto.getManager_wardnumber());
        ma.setManagerDistrict(managerDto.getManagerDistrict());
        ma.setManagerLocalLevel(managerDto.getManagerLocalLevel());
        ma.setManagerProvision(managerDto.getManagerProvision());
        Role role = this.roleRepo.findById(AppConstants.BRANCH_MANAGER_USER).get();
        user.getRoles().clear();
	    user.getRoles().add(role);		   
        Manager savedManager = managerRepo.save(ma);
        String welcomeMessage = String.format(
        	    "Welcome, %s! You have been appointed as the manager of %s. Thank you for joining us.",
        	    user.getName(), branch.getName()
        	);

        emailService.sendOtpMobile(user.getMobileNo(), welcomeMessage);
        
        if(user.getEmail()!=null) {
        String subject = "Welcome as Branch Manager";
        emailService.sendOtpEmail(user.getEmail(), subject, welcomeMessage);
        }
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setMessage(
        		String.format("Hello %s, you are now the manager of %s.", user.getName(), branch.getName())
        		);
          
           notificationService.createNotification(notificationDto, user.getId());
           
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
