package com.ride_share.service.impl;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ride_share.config.AppConstants;
import com.ride_share.entities.Branch;
import com.ride_share.entities.OtpRequest;
import com.ride_share.entities.Rider;
import com.ride_share.entities.RiderApprovalRequest;
import com.ride_share.entities.Role;
import com.ride_share.entities.User;
import com.ride_share.entities.User.UserMode;
import com.ride_share.exceptions.ApiException;
import com.ride_share.exceptions.ResourceNotFoundException;
import com.ride_share.playoads.Location;
import com.ride_share.playoads.ManagerAddress;
import com.ride_share.playoads.UserDto;
import com.ride_share.playoads.VerificationDto;
import com.ride_share.repositories.BranchRepo;
import com.ride_share.repositories.OtpRequestRepo;
import com.ride_share.repositories.RiderApprovalRequestRepo;
import com.ride_share.repositories.RiderRepo;
import com.ride_share.repositories.RoleRepo;
import com.ride_share.repositories.UserRepo;
import com.ride_share.service.OtpRequestService;
import com.ride_share.service.UserService;


@Service
public class UserServiceImpl implements UserService {
	  private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
	    private static final long OTP_VALID_DURATION = 5 * 60; // 5 min in seconds
	    
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
		
		   @Autowired
		   private OtpRequestService sendmsg;
		   
		   @Autowired
		   private BranchRepo branchRepo;
		   
		   @Autowired
		   private RiderApprovalRequestRepo approvalRequestRepo;
		   @Autowired
			private VerificationService verificationService;
		   
		   
		   
		   
		   
		   @Override
			public UserDto registerNewUser(UserDto userDto) {
			    User user = this.modelMapper.map(userDto, User.class);
	    
			    String emailKey = "email:" + userDto.getEmail();
			    String mobileKey = "mobile:" + userDto.getMobileNo();

			    VerificationDto emailOtp = verificationService.getOtpDetails(emailKey);
			    VerificationDto mobileOtp = verificationService.getOtpDetails(mobileKey);

			    VerificationDto validOtp = null;
			    String validKey = null;

			    if (emailOtp != null && emailOtp.getOtp().equals(userDto.getOtp())) {
			        validOtp = emailOtp;
			        validKey = emailKey;
			    } else if (mobileOtp != null && mobileOtp.getOtp().equals(userDto.getOtp())) {
			        validOtp = mobileOtp;
			        validKey = mobileKey;
			    } else {
			        throw new ApiException("Invalid OTP!");
			    }

			    // Time validation
			    if (Duration.between(validOtp.getTimestamp(), Instant.now()).getSeconds() > OTP_VALID_DURATION) {
			        verificationService.removeOtp(validKey);
			        throw new ApiException("OTP expired! Please request a new one.");
			    }

			    // OTP valid => remove it
			    verificationService.removeOtp(validKey);

	            user.setImageName("");
			    // encoded the password
			    user.setPassword(this.passwordEncoder.encode(user.getPassword()));

	           // String otp = userDto.getOtp();
	            user.setMobileNo(userDto.getMobileNo());
	            user.setEmail(userDto.getEmail());
	            user.setDateOfRegistration(LocalDateTime.now());
			    // roles
			    Role role = this.roleRepo.findById(AppConstants.NORMAL_USER).get();
			    user.getRoles().add(role);		   
			    user.setModes(UserMode.PESSENGER);
			    
		        User newUser = this.userRepo.save(user);
		        
		        String welcomeMessage = String.format("Welcome, %s! We're excited to have you on our Ride-Share. Dive in and enjoy the journey ahead! "
		        		+ "Thank you for choosing us, Tuffan", user.getName());
		        sendmsg.sendMessage(user.getMobileNo(), welcomeMessage); // Assuming notificationService sends SMS

		     // Create in-app notification
		     //   notificationService.createNotification(newUser.getId(), welcomeMessage);	   
			    return this.modelMapper.map(newUser, UserDto.class);
			}
		   
		   
		   
		   
		   
		   
		   
		   
		   
		   
		   
		   
		   
		   
		   
		   
		   
		   @Override
		   public UserDto updateCurrentLocation(UserDto userDto, Integer userId) {
			    User user = this.userRepo.findById(userId)
			            .orElseThrow(() -> new ResourceNotFoundException("User", "Id", userId));

			    // Update the currentLocation field with the new values from userDto
			    Location newLocation = userDto.getCurrentLocation(); // Get Location object
			    newLocation.setTimestamp(LocalDateTime.now());
			    user.setCurrentLocation(newLocation); // Update the entire Location object

			    // Save the updated user back to the repository
			    User updatedUser = this.userRepo.save(user);

			    // Convert the updated User to UserDto and return it
			    return this.userToDto(updatedUser);
			}


		   
		   
		   
		   @Override
		   public UserDto updateManager(UserDto userDto, Integer userId, Integer branchId) {
		       User user = this.userRepo.findById(userId)
		               .orElseThrow(() -> new ResourceNotFoundException("User", "Id", userId));

		       Branch branch = this.branchRepo.findById(branchId)
		               .orElseThrow(() -> new ResourceNotFoundException("Branch", "Id", branchId));

		       // Initialize or get existing managerAddress
		       ManagerAddress managerAddress = user.getManagerAddress();
		       if (managerAddress == null) {
		           managerAddress = new ManagerAddress();
		       }

		       ManagerAddress managerAddressDto = userDto.getManagerAddress();
		       if (managerAddressDto != null) {
		           if (managerAddressDto.getManagerProvision() != null &&
		               !managerAddressDto.getManagerProvision().equals(managerAddress.getManagerProvision())) {
		               managerAddress.setManagerProvision(managerAddressDto.getManagerProvision());
		           }

		           if (managerAddressDto.getManagerLocalLevel() != null &&
		               !managerAddressDto.getManagerLocalLevel().equals(managerAddress.getManagerLocalLevel())) {
		               managerAddress.setManagerLocalLevel(managerAddressDto.getManagerLocalLevel());
		           }

		           if (managerAddressDto.getManagerDistrict() != null &&
		               !managerAddressDto.getManagerDistrict().equals(managerAddress.getManagerDistrict())) {
		               managerAddress.setManagerDistrict(managerAddressDto.getManagerDistrict());
		           }

		           if (managerAddressDto.getManager_wardnumber() != null &&
		               !managerAddressDto.getManager_wardnumber().equals(managerAddress.getManager_wardnumber())) {
		               managerAddress.setManager_wardnumber(managerAddressDto.getManager_wardnumber());
		           }

		           managerAddress.setBranch(branch);
		           user.setManagerAddress(managerAddress);
		       }

		       User updatedUser = this.userRepo.save(user);
		       return this.userToDto(updatedUser);
		   }



		   

			public User dtoToUser(UserDto userDto) {
				User user = this.modelMapper.map(userDto, User.class);

				return user;
			}

			public UserDto userToDto(User user) {
				UserDto userDto = this.modelMapper.map(user, UserDto.class);
				return userDto;
			}
			
		   
		   
		   
		   
		   
		
		
		
		@Override
		public UserDto updateUser(UserDto userDto, Integer userId) {
		    User user = this.userRepo.findById(userId)
		            .orElseThrow(() -> new ResourceNotFoundException("User", "Id", userId));

		    // Rule: If mobile number or email is being changed, password must be validated
		    boolean isMobileChanged = userDto.getMobileNo() != null && !userDto.getMobileNo().equals(user.getMobileNo());
		    boolean isEmailChanged = userDto.getEmail() != null && !userDto.getEmail().equals(user.getEmail());

		    if (isMobileChanged || isEmailChanged) {
		        if (userDto.getPassword() == null || !passwordEncoder.matches(userDto.getPassword(), user.getPassword())) {
		            throw new ApiException("Current password required to change mobile number or email.");
		        }

		        // Now safe to update
		        if (isMobileChanged) {
		            user.setMobileNo(userDto.getMobileNo());
		        }
		        if (isEmailChanged) {
		            user.setEmail(userDto.getEmail());
		        }
		    }

		    // Always allow these to update
		    user.setName(userDto.getName());
		    user.setImageName(userDto.getImageName());
		    

		    User updatedUser = this.userRepo.save(user);
		    return this.modelMapper.map(updatedUser, UserDto.class);
		}

		
		@Override
		public UserDto createUser(UserDto userDto) {
		    User userEntity = this.dtoToUser(userDto);
		    User savedUser = this.userRepo.save(userEntity);
		    return this.userToDto(savedUser);
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


	
	
	//---------------------------------------

	@Override
	public void addRoleToUser(String email, String roleName) {
		   // Fetch user by email, throw exception if not found
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        // Fetch role by name, throw exception if not found
        Role role = roleRepo.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleName));

        // Clear existing roles and assign new role
        user.getRoles().clear();  // Clear all existing roles
        user.getRoles().add(role);  // Assign new role
        user.setDate_Of_Role_Changed(LocalDateTime.now());  // Update role change date
        
        // Save updated user
        userRepo.save(user);
        System.out.println("User role changed to " + roleName + ".");
	}

	
	@Override
    public UserDto getUserByEmail(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return modelMapper.map(user, UserDto.class);
    }

	@Override
	public List<UserDto> getUsersByRole(String roleName) {
		Role role = roleRepo.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleName));
        return userRepo.findAll().stream()
                .filter(user -> user.getRoles().contains(role))
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    
	}

	@Override
	public UserDto updatePassword(UserDto userDto, Integer userId) {
		User user = this.userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "Id", userId));

		String otp=userDto.getOtp();
		if(otp==null) {
			throw new IllegalArgumentException("OTP must be provided");
		}
		// Fetch the OTP from the database
	    List<OtpRequest> otpRequests = this.otpRepo.findByOtp(otp);
	    OtpRequest validOtpRequest = null;
	    for (OtpRequest otpRequest : otpRequests) {
	        if (otpRequest.getOtp() != null && otpRequest.getOtp().equals(otp)) {
	            LocalDateTime otpValidUntil = otpRequest.getOtpValidUntil();
	            if (otpValidUntil != null) {
	                Instant otpValidUntilInstant = otpValidUntil.atZone(ZoneId.systemDefault()).toInstant();
	                Instant now = Instant.now();
	                if (otpValidUntilInstant.isAfter(now)) {
	                    validOtpRequest = otpRequest;
	                    break; // Found valid OTP, exit loop
	                }
	            }
	        }
	    }
	    if (validOtpRequest == null) {
	        throw new IllegalArgumentException("Invalid or expired OTP");
	    }
	    // OTP is valid, proceed to update the password
	    user.setPassword(passwordEncoder.encode(userDto.getPassword()));
	    User updatedUser = userRepo.save(user);

	    return modelMapper.map(updatedUser, UserDto.class);
		
	}

//	@Override
//	public UserDto GetOtp(UserDto userDto, Integer userId) {
//		  User user = this.userRepo.findById(userId)
//	                .orElseThrow(() -> new ResourceNotFoundException("User", "Id", userId));
//
//	        // Saving OTP to User table (optional step)
//	        user.setOtp(userDto.getOtp());
//	        
//	        // Save and return updated user with OTP
//	        return modelMapper.map(userRepo.save(user), UserDto.class);
//	}
	
	@Override
	 public Location getLocationByUserId(Integer userId) {
	        Location location = userRepo.findLocationByUserId(userId);
	        if (location == null) {
	            throw new ResourceNotFoundException("User", "Id", userId);
	        }
	        return location;
	    }
	@Override
	public UserDto UserModeChanger(Integer userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "User id", userId));

        if (user.getModes() == UserMode.RIDER || user.getModes()==null) {
            
	user.setModes(UserMode.PESSENGER);
        }else{
	user.setModes(UserMode.RIDER);
}
        userRepo.save(user);
        return modelMapper.map(user, UserDto.class);
   }
	
	@Override
	public void sendResetPasswordOtp(String emailOrMobile) {
	    // Ensure user exists before sending OTP
	    userRepo.findByEmail(emailOrMobile)
	        .or(() -> userRepo.findByMobileNo(emailOrMobile))
	        .orElseThrow(() -> new ResourceNotFoundException("User", "emailOrMobile", emailOrMobile));

	    // Send OTP via VerificationService (your existing logic)
	    verificationService.sendOtpForReset(emailOrMobile);
	}
	@Override
	public void resetPassword(String emailOrMobile, String otp, String newPassword) {
	    boolean isValid = verificationService.verifyOtp(emailOrMobile, otp);

	    if (!isValid) {
	        throw new ApiException("Invalid or expired OTP");
	    }

	    User user = userRepo.findByEmail(emailOrMobile)
	        .or(() -> userRepo.findByMobileNo(emailOrMobile))
	        .orElseThrow(() -> new ResourceNotFoundException("User", "emailOrMobile", emailOrMobile));

	    user.setPassword(passwordEncoder.encode(newPassword));
	    userRepo.save(user);

	    verificationService.removeOtp(emailOrMobile);  // Remove after successful reset
	}



	
//	public UserDto UserBlanceUpdate(UserDto userDto,Integer userId) {
//		User user = userRepo.findById(userId)
//                .orElseThrow(() -> new ResourceNotFoundException("User", "User id", userId));
//		Rider.RiderStatus existingStatus = this.riderRepo.findRiderStatusByUserId(userId);
//        if (existingStatus == Rider.RiderStatus.PENDING || existingStatus == Rider.RiderStatus.REJECTED) {
//            throw new IllegalStateException("Cannot create rider. User already has a rider application in PENDING status.");
//        }
//        
//        user.setBalance(userDto.getBalance());
//
//	}
	
}
