package com.ride_share;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ride_share.config.AppConstants;
import com.ride_share.entities.Branch;
import com.ride_share.entities.Role;
import com.ride_share.repositories.BranchRepo;
import com.ride_share.repositories.RoleRepo;
import com.ride_share.service.impl.PricingServiceImpl;

@SpringBootApplication
//@SpringBootTest(classes = RideShareApplication.class)
public class RideShareApplication implements CommandLineRunner{

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private RoleRepo roleRepo;
	
//	@Autowired
//	private BranchRepo branchRepo;

	
	public static void main(String[] args) {
		SpringApplication.run(RideShareApplication.class, args);
		
		
	}

	
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println(this.passwordEncoder.encode("xyz"));

		try {
			
			  Role role1 = new Role();
		        role1.setId(AppConstants.NORMAL_USER);
		        role1.setName("ROLE_NORMAL");
		        
	        Role role2 = new Role();
	        role2.setId(AppConstants.ADMIN_USER);
	        role2.setName("ROLE_ADMIN");

	      
	        Role role3 = new Role();
	        role3.setId(AppConstants.RIDER_USER);
	        role3.setName("ROLE_RIDER");

	        
	        Role role4=new Role();
	        role4.setId(AppConstants.BRANCH_MANAGER_USER);
	        role4.setName("Role_B-MANAGER");
	        
	        Role role5=new Role();
	        role5.setId(AppConstants.SUPER_ADMIN_USER);
	        role5.setName("ROLE_SUPER_ADMIN");
	        
	        List<Role> roles = List.of(role1, role2, role3 , role4, role5);

	        List<Role> result = this.roleRepo.saveAll(roles);
            
	        result.forEach(r -> {
	            System.out.println(r.getName());
	        });
		
//	        
//	        if (this.branchRepo.count() == 0) {
//	        
//	        Branch branch1 = new Branch();
//	        branch1.setId(AppConstants.DAMAK_BRANCH);
//	        branch1.setName("Damak,Koshi province");
//	       
//	        Branch branch2 = new Branch();
//	        branch1.setId(AppConstants.KATHMANDU_BRANCH);
//	        branch2.setName("Kathmandu,Bagmati province");
//	      
//	        List<Branch>branchs=List.of(branch1,branch2);
//	        List<Branch>result2=this.branchRepo.saveAll(branchs);
//	        result2.forEach(r->{
//	        	
//	        System.out.printf(r.getName());
//	        });
//	        
//	        } else {
//	            System.out.println("Branches already exist, skipping initialization.");
//	        }
//	        
		} catch (Exception e) {
			e.printStackTrace();
		}
	}}
		

