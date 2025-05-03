package com.ride_share.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ride_share.entities.Branch;

import com.ride_share.entities.User;
import com.ride_share.exceptions.ApiException;
import com.ride_share.playoads.ApiResponse;
import com.ride_share.playoads.EmergencyDTO;
import com.ride_share.playoads.JwtAuthRequest;
import com.ride_share.playoads.JwtAuthResponse;
import com.ride_share.playoads.OtpRequestDto;
import com.ride_share.playoads.PriceInfoDto;
import com.ride_share.playoads.ResetPasswordRequestDto;
import com.ride_share.playoads.RideRequestDto;
import com.ride_share.playoads.UserDto;
import com.ride_share.repositories.BranchRepo;
import com.ride_share.repositories.UserRepo;
import com.ride_share.security.JwtTokenHelper;


import com.ride_share.service.RideRequestService;
import com.ride_share.service.UserService;

import com.ride_share.service.impl.RateLimitingService;
import com.ride_share.service.impl.VerificationService;

@RestController
@RequestMapping("/api/v1/auth/")
public class AuthController {
	@Autowired
	private JwtTokenHelper jwtTokenHelper;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserService userService;
	
	 @Autowired
	    private RideRequestService rideRequestService;

	  
	 @Autowired
	 private RateLimitingService rateLimitingService;
	 
	 @Autowired
	    private BranchRepo branchRepo;
	 
	 @Autowired
	 private VerificationService verificationService;

     @GetMapping("/emergency")
     public List<EmergencyDTO> getAllEmergencies() {
         List<EmergencyDTO> list = new ArrayList<>();
         list.add(new EmergencyDTO("Fire Department", "101", "Damak"));
         list.add(new EmergencyDTO("Police Station", "100", "Damak"));
         list.add(new EmergencyDTO("Hospital", "102", "Damak"));
         return list;
     }
	 
	 @PostMapping("/send")
	 public ResponseEntity<ApiResponse> sendOtp(@RequestParam("input") String emailOrMobile) {
	     verificationService.sendOtp(emailOrMobile);
	     ApiResponse response = new ApiResponse( "OTP sent to: " + emailOrMobile,true);
	     return ResponseEntity.ok(response);
	 }


	 @PostMapping("/verify")
	 public ResponseEntity<ApiResponse> verify(@RequestBody OtpRequestDto request) {
	     ApiResponse response = userService.verifyUser(request.getEmailOrMobile(), request.getOtp());
	     return ResponseEntity.ok(response);
	 }

	 
	 @PostMapping("/forgetpw")
	 public ResponseEntity<ApiResponse> sendResetOtp(@RequestBody ResetPasswordRequestDto request) {
	     String emailOrMobile = request.getEmailOrMobile();
	     userService.sendResetPasswordOtp(emailOrMobile);
	     
	     String target = emailOrMobile.contains("@") ? "email." : "mobile.";
	     return ResponseEntity.ok(new ApiResponse("OTP sent successfully to your " + target, true));
	 }



	    @PostMapping("/update-password")
	    public ResponseEntity<ApiResponse> resetPassword(@RequestBody ResetPasswordRequestDto request) {
	        userService.resetPassword(request.getEmailOrMobile(), request.getOtp(), request.getNewPassword());
	        return ResponseEntity.ok(new ApiResponse("Password Changed successfully.",true));

	    }

	@PostMapping("/login")
	public ResponseEntity<JwtAuthResponse> createToken(@RequestBody JwtAuthRequest request) throws Exception {
		this.authenticate(request.getUsername(), request.getPassword());
		UserDetails userDetails = this.userDetailsService.loadUserByUsername(request.getUsername());
		String token = this.jwtTokenHelper.generateToken(userDetails);

		JwtAuthResponse response = new JwtAuthResponse();
		response.setToken(token);
		response.setUser(this.mapper.map((User) userDetails, UserDto.class));
		return new ResponseEntity<JwtAuthResponse>(response, HttpStatus.OK);
	}

	private void authenticate(String username, String password) throws Exception {

		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,
				password);

		try {

			this.authenticationManager.authenticate(authenticationToken);

		} catch (BadCredentialsException e) {
			System.out.println("Invalid Detials !!");
			throw new ApiException("Invalid username or password !!");
		}

	}

	// register new user api

	@PostMapping("/register")
	public ResponseEntity<UserDto> registerUser(@Valid @RequestBody UserDto userDto) {
		UserDto registeredUser = this.userService.registerNewUser(userDto);
		return new ResponseEntity<UserDto>(registeredUser, HttpStatus.CREATED);
	}

	// get loggedin user data
	@Autowired
	private UserRepo userRepo;
	@Autowired
	private ModelMapper mapper;

	@GetMapping("/current-user/")
	public ResponseEntity<UserDto> getUser(Principal principal) {
		User user = this.userRepo.findByEmail(principal.getName()).get();
		return new ResponseEntity<UserDto>(this.mapper.map(user, UserDto.class), HttpStatus.OK);
	}
	

    @GetMapping("/branch")
    public List<Branch> getAllBranches() {
        return branchRepo.findAll();
    }  
    
  
    
    @PostMapping("/ride/price")
    public ResponseEntity<PriceInfoDto> getPriceInfo(
            @RequestBody RideRequestDto rideRequestDto,
            @RequestParam Integer userId,
            @RequestParam Integer categoryId
    ) {
        PriceInfoDto priceInfo = rideRequestService.determinePrice(rideRequestDto, userId, categoryId);
        return ResponseEntity.ok(priceInfo);
    }
}
