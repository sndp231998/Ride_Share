package com.bus_ticket.service.impl;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bus_ticket.config.AppConstants;
import com.bus_ticket.entities.Role;
import com.bus_ticket.entities.User;
import com.bus_ticket.exceptions.ResourceNotFoundException;
import com.bus_ticket.playoads.UserDto;
import com.bus_ticket.repositories.UserRepo;
import com.bus_ticket.service.UserService;
import com.bus_ticket.repositories.OtpRequestRepo;
import com.bus_ticket.entities.OtpRequest;
import com.bus_ticket.repositories.RoleRepo;


@Service
public class UserServiceImpl implements UserService {
	  private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");

	    
	    @Autowired
	    private UserRepo userRepo;

	    @Autowired
	    private ModelMapper modelMapper;

	    @Autowired
	    private PasswordEncoder passwordEncoder;

		@Autowired
		private RoleRepo roleRepo;
	    
		@Autowired
	    private OtpRequestRepo otpRepo;
		
		
//-------------------------------------user------------------------------------------------------------
		@Override
		public UserDto registerNewUser(UserDto userDto) {
		    User user = this.modelMapper.map(userDto, User.class);

		    // encoded the password
		    user.setPassword(this.passwordEncoder.encode(user.getPassword()));
            user.setImageName("");
            user.setMobileNo(userDto.getMobileNo());
		    // roles
		    Role role = this.roleRepo.findById(AppConstants.NORMAL_USER).get();
		    user.getRoles().add(role);

		    String otp = userDto.getOtp();
		    // Logging OTP from user
	        logger.info("Otp from user: " + otp);
	        
	        if (otp == null) {
	            throw new IllegalArgumentException("OTP must be provided");
	        }
	        
	        // Get OTP requests from the repository
	        List<OtpRequest> otpRequests = this.otpRepo.findByOtp(otp);
	        logger.info("Retrieved OTP requests: " + otpRequests);
	        
		    User newUser = this.userRepo.save(user);
		    return this.modelMapper.map(newUser, UserDto.class);
		}
		
		@Override
		public UserDto createUser(UserDto userDto) {
		    User userEntity = this.dtoToUser(userDto);
		    User savedUser = this.userRepo.save(userEntity);
		    return this.userToDto(savedUser);
		}



	@Override
	public UserDto updateUser(UserDto userDto, Integer userId) {
		User user = this.userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "Id", userId));

        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
       user.setMobileNo(userDto.getMobileNo());
       user.setImageName(userDto.getImageName());
       
        User updatedUser = this.userRepo.save(user);
        return this.userToDto(updatedUser);
        
	}

	@Override
	public UserDto getUserById(Integer userId) {
		 User user = this.userRepo.findById(userId)
	                .orElseThrow(() -> new ResourceNotFoundException("User", "Id", userId));

	        return this.userToDto(user);
	}

	@Override
	public List<UserDto> getAllUsers() {
		 List<User> users = this.userRepo.findAll();
	        return users.stream().map(this::userToDto).collect(Collectors.toList());
	    }

	@Override
	public void deleteUser(Integer userId) {
		  User user = this.userRepo.findById(userId)
	                .orElseThrow(() -> new ResourceNotFoundException("User", "Id", userId));
	      user.getRoles().clear();
	        this.userRepo.delete(user);
	    }
	
	//----------------------------------------------------------------------------------

	public User dtoToUser(UserDto userDto) {
		User user = this.modelMapper.map(userDto, User.class);

		return user;
	}

	public UserDto userToDto(User user) {
		UserDto userDto = this.modelMapper.map(user, UserDto.class);
		return userDto;
	}
	
	
}
