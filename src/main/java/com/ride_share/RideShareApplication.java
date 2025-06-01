package com.ride_share;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ride_share.config.AppConstants;
import com.ride_share.entities.Branch;
import com.ride_share.entities.Role;
import com.ride_share.entities.User;
import com.ride_share.repositories.BranchRepo;
import com.ride_share.repositories.RoleRepo;
import com.ride_share.repositories.UserRepo;
import com.ride_share.service.impl.PricingServiceImpl;
@EnableScheduling
@SpringBootApplication
//@SpringBootTest(classes = RideShareApplication.class)
public class RideShareApplication implements CommandLineRunner{

	
	@Autowired
	private RoleRepo roleRepo;
	
	
	public static void main(String[] args) {
		SpringApplication.run(RideShareApplication.class, args);
		
		
	}

	
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	@Override
	public void run(String... args) throws Exception {

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
	        
	     
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
	}}
		

