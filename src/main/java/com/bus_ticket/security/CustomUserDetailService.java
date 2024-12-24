package com.bus_ticket.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bus_ticket.entities.User;
import com.bus_ticket.exceptions.ResourceNotFoundException;
import com.bus_ticket.repositories.UserRepo;



@Service
public class CustomUserDetailService implements UserDetailsService  {

	@Autowired
	private UserRepo userRepo;

	 @Override
	    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {
	        User user;
	        // Check if the input is an email or mobile number
	        if (input.matches("^\\d+$")) { // Regex to check if the input is all digits
	            // It's a mobile number
	            user = this.userRepo.findByMobileNo(input)
	                    .orElseThrow(() -> new ResourceNotFoundException("User", "mobileNo: " + input, 0));
	        } else if (input.contains("@")) {
	            // It's an email
	            user = this.userRepo.findByEmail(input)
	                    .orElseThrow(() -> new ResourceNotFoundException("User", "email: " + input, 0));
	        } else {
	            throw new UsernameNotFoundException("Invalid username format");
	        }

	        return (UserDetails) user;
	    }
	

	
}
