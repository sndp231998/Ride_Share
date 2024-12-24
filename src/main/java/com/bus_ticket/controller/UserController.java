package com.bus_ticket.controller;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bus_ticket.playoads.ApiResponse;
import com.bus_ticket.playoads.UserDto;
import com.bus_ticket.service.UserService;
import com.bus_ticket.service.impl.RateLimitingService;





@RestController
@RequestMapping("/api/v1/users")
public class UserController {

	 private static final Logger logger = LoggerFactory.getLogger(UserController.class);
		@Autowired
		private UserService userService;
		
//		@Autowired
//		private FileService fileService;

		@Value("${project.image}")
		private String path;
	 @Autowired
		    private RateLimitingService rateLimitingService;
		
		
		
		@PostMapping("/")
		public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
			UserDto createUserDto = this.userService.createUser(userDto);
			return new ResponseEntity<>(createUserDto, HttpStatus.CREATED);
		}

		@PutMapping("/{userId}")
		public ResponseEntity<UserDto> updateUser(@Valid @RequestBody UserDto userDto, @PathVariable("userId") Integer uid) {
			UserDto updatedUser = this.userService.updateUser(userDto, uid);
			return ResponseEntity.ok(updatedUser);
		}

		@PreAuthorize("hasRole('ADMIN')")
		@DeleteMapping("/{userId}")
		public ResponseEntity<ApiResponse> deleteUser(@PathVariable("userId") Integer uid) {
			this.userService.deleteUser(uid);
			return new ResponseEntity<ApiResponse>(new ApiResponse("User deleted Successfully", true), HttpStatus.OK);
		}
		
		@PreAuthorize("hasRole('ADMIN')")
		@GetMapping("/")
		public ResponseEntity<List<UserDto>> getAllUsers() {
			 rateLimitingService.checkRateLimit("test-api-key");
			return ResponseEntity.ok(this.userService.getAllUsers());
		}
		@GetMapping("/{userId}")
		public ResponseEntity<UserDto> getSingleUser(@PathVariable Integer userId) {
			return ResponseEntity.ok(this.userService.getUserById(userId));
		}
		
}
