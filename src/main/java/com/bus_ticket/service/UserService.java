package com.bus_ticket.service;

import java.util.List;

import com.bus_ticket.playoads.UserDto;



public interface UserService {

	
	
	UserDto registerNewUser(UserDto user);
	
	//-----------User GetOnlyPhoneNum(User user);------------

		UserDto createUser(UserDto user);

		UserDto updateUser(UserDto user, Integer userId);

		UserDto getUserById(Integer userId);

		List<UserDto> getAllUsers();

		void deleteUser(Integer userId);
		
		
}
