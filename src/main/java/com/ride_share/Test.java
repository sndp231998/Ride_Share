//package com.ride_share;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import com.ride_share.config.AppConstants;
//import com.ride_share.entities.Role;
//import com.ride_share.entities.User;
//import com.ride_share.repositories.RoleRepo;
//import com.ride_share.repositories.UserRepo;
//import com.ride_share.service.impl.PricingServiceImpl;
//
//@Component
//public class Test implements CommandLineRunner{
//
//	@Autowired
//	private UserRepo userRepo;
//	@Autowired
//	private RoleRepo roleRepo;
//	@Autowired
//	private PasswordEncoder passwordEncoder;
//	@Autowired
//	PricingServiceImpl p;
//	@Override
//	public void run(String... args) throws Exception {
//		
//		// Check if super admin user exists
//        Optional<User> optionalUser = userRepo.findByEmail("superadmin@gmail.com");
//        if (optionalUser.isEmpty()) {
//            User user = new User();
//            user.setName("Super Admin");
//            user.setEmail("superadmin@gmail.com");
//            user.setDateOfRegistration(LocalDateTime.now());
//            user.setMobileNo("superadmin@gmail.com");
//            user.setPassword(passwordEncoder.encode("231998")); // encrypted passwor
//            Role role = this.roleRepo.findById(AppConstants.ADMIN_USER).get();
//		    user.getRoles().add(role);	
//            
//              userRepo.save(user);
//            System.out.println("✅ Super Admin user created");
//        } else {
//            System.out.println("ℹ️ Super Admin user already exists");
//        }
//	}
//
//}
