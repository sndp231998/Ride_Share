package com.bus_ticket;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.bus_ticket.config.AppConstants;
import com.bus_ticket.entities.Role;
import com.bus_ticket.repositories.RoleRepo;



@SpringBootApplication
public class BusTicketBookingApplication implements CommandLineRunner{

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private RoleRepo roleRepo;
	
	
	public static void main(String[] args) {
		SpringApplication.run(BusTicketBookingApplication.class, args);
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
		        
	        Role role = new Role();
	        role.setId(AppConstants.ADMIN_USER);
	        role.setName("ROLE_ADMIN");

	      
	        Role role2 = new Role();
	        role2.setId(AppConstants.AGENT_USER);
	        role2.setName("ROLE_AGENT");

	        
	        Role role3=new Role();
	        role3.setId(AppConstants.STAFF_USER);
	        role3.setName("Role_STAFF");
	        
	        Role role4=new Role();
	        role4.setId(AppConstants.SUPER_ADMIN_USER);
	        role4.setName("ROLE_SUPER_ADMIN");
	        
	        List<Role> roles = List.of(role, role1, role2 , role3,role4);

	        List<Role> result = this.roleRepo.saveAll(roles);

	        result.forEach(r -> {
	            System.out.println(r.getName());
	        });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
