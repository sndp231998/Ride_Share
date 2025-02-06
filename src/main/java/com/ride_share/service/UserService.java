package com.ride_share.service;

import java.util.List;

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
	    UserDto updatePassword(UserDto user ,Integer userId);
	    UserDto GetOtp(UserDto user,Integer userId);

		UserDto UserModeChanger(Integer userId);
}
