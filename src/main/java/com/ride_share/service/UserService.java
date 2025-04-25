package com.ride_share.service;

import java.util.List;
import java.util.Set;

import com.ride_share.playoads.ApiResponse;
import com.ride_share.playoads.Location;
import com.ride_share.playoads.UserDto;



public interface UserService {

	
	
	UserDto registerNewUser(UserDto user);
	
	//-----------User GetOnlyPhoneNum(User user);------------

		UserDto createUser(UserDto user);

		UserDto updateUser(UserDto user, Integer userId);

		UserDto getUserById(Integer userId);

		List<UserDto> getAllUsers();

		void deleteUser(Integer userId);
		
		
		//--------------------role change------------------------
		void addRoleToUser(String email, String roleName);
		
		//void updateUserRoles();
		
		//void sendSubscriptionExpiryWarnings();
		
	    UserDto getUserByEmail(String email);
	    
	    List<UserDto> getUsersByRole(String roleName);
	    
	   // List<UserDto>getUsersByPayment(String )
		
	//---------------forget password---------------
	   // UserDto updatePassword(UserDto user ,Integer userId);
	    //UserDto GetOtp(UserDto user,Integer userId);

		UserDto UserModeChanger(Integer userId);

		void sendResetPasswordOtp(String emailOrMobile);
		void resetPassword(String emailOrMobile, String otp, String newPassword);


		UserDto updateManager(UserDto userDto, Integer userId, Integer branchId);

		UserDto updateCurrentLocation(UserDto userDto, Integer userId);

		Location getLocationByUserId(Integer userId);

		ApiResponse verifyUser(String emailOrMobile, String otp);

}
