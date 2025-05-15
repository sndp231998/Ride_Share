package com.ride_share.service.impl;

import java.util.List;

import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ride_share.entities.Branch;
import com.ride_share.exceptions.ResourceNotFoundException;
import com.ride_share.playoads.BranchDto;
import com.ride_share.repositories.BranchRepo;
import com.ride_share.service.BranchService;

@Service
public class BranchServiceImpl implements BranchService{

	@Autowired
	private BranchRepo branchRepo;

	@Autowired
	private ModelMapper modelMapper;
	
	@Override
	public BranchDto CreateBranch(BranchDto branchDto) {
		Branch bra= this.modelMapper.map(branchDto, Branch.class);
		bra.setBranchCode(branchDto.getBranchCode());
		bra.setProvince(branchDto.getProvince());
		bra.setDistrict(branchDto.getDistrict());
		bra.setLocalLevel(branchDto.getLocalLevel());
		bra.setPhoneNo(branchDto.getPhoneNo());
		bra.setEmail(branchDto.getEmail());
//		bra.setBaseFare(branchDto.getBaseFare());
//		bra.setPerKmRate(branchDto.getPerKmRate());
		
		Branch savebranch=branchRepo.save(bra);
		return modelMapper.map(savebranch,BranchDto.class);
	}

	@Override
    public BranchDto updateBranch(BranchDto branchDto, Integer branchId) {
        Branch branch = this.branchRepo.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch", "Id", branchId));
        
        branch.setBranchCode(branchDto.getBranchCode());
        branch.setProvince(branchDto.getProvince());
        branch.setDistrict(branchDto.getDistrict());
        branch.setLocalLevel(branchDto.getLocalLevel());
        branch.setPhoneNo(branchDto.getPhoneNo());
        branch.setEmail(branchDto.getEmail());
//        branch.setBaseFare(branchDto.getBaseFare());
//        branch.setPerKmRate(branchDto.getPerKmRate());
        
        Branch updatedBranch = this.branchRepo.save(branch);
        return this.modelMapper.map(updatedBranch, BranchDto.class);
    }

	@Override
    public void deleteBranch(Integer branchId) {
        Branch branch = this.branchRepo.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch", "Id", branchId));
        this.branchRepo.delete(branch);
    }

	@Override
    public List<BranchDto> getAllBranch() {
        List<Branch> branches = this.branchRepo.findAll();
        return branches.stream()
                .map(branch -> this.modelMapper.map(branch, BranchDto.class))
                .collect(Collectors.toList());
    }
	
	@Override
    public BranchDto getBranchById(Integer branchId) {
        Branch branch = this.branchRepo.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch", "Id", branchId));
        return this.modelMapper.map(branch, BranchDto.class);
    }
}
