package com.ride_share.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.io.FilenameUtils;
import org.hibernate.engine.jdbc.StreamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ride_share.playoads.ApiResponse;
import com.ride_share.playoads.UserDto;
import com.ride_share.service.FileService;
import com.ride_share.service.UserService;
import com.ride_share.service.impl.RateLimitingService;


@RestController
@RequestMapping("/api/v1/users")
public class UserController {

	 private static final Logger logger = LoggerFactory.getLogger(UserController.class);
		@Autowired
		private UserService userService;
		
	   @Autowired
 	    private FileService fileService;

		@Value("${project.image}")
		private String path;
	 @Autowired
		    private RateLimitingService rateLimitingService;
		
		
		
		@PostMapping("/")
		public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
			UserDto createUserDto = this.userService.createUser(userDto);
			return new ResponseEntity<>(createUserDto, HttpStatus.CREATED);
		}

		//@Valid annotation lauda user ko body ko sabai halnu parne hunxa, so nahalako
		@PutMapping("/{userId}")
		public ResponseEntity<UserDto> updateUser( @RequestBody UserDto userDto, @PathVariable("userId") Integer uid) {
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
		//-----------------ROles change----------------
		@PreAuthorize("hasRole('ADMIN')")
		@PostMapping("/addRole/email/{email}/role/{roleName}")
		public ResponseEntity<ApiResponse> addRoleToUser(@PathVariable String email, @PathVariable String roleName) {
		    	    userService.addRoleToUser(email, roleName);
		    ApiResponse response = new ApiResponse("Role added successfully", true);
		    return ResponseEntity.status(HttpStatus.OK).body(response);
		}

		@GetMapping("/email/{email}")
	    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email) {
	        UserDto user = userService.getUserByEmail(email);
	        return new ResponseEntity<>(user, HttpStatus.OK);
	    }
		
		@PreAuthorize("hasRole('ADMIN')")
	    @GetMapping("/role/{roleName}")
	    public ResponseEntity<List<UserDto>> getUsersByRole(@PathVariable String roleName) {
	        List<UserDto> users = userService.getUsersByRole(roleName);
	        return new ResponseEntity<>(users, HttpStatus.OK);
	    }
		
		//-------------Image upload-------------------
		// Post method for file upload
	    @PostMapping("/file/upload/{userId}")
	    public ResponseEntity<UserDto> uploadUserFile(@RequestParam("file") MultipartFile file,
	                                                  @PathVariable Integer userId) throws IOException {
	        UserDto userDto = this.userService.getUserById(userId);
	        String fileName = this.fileService.uploadFile(path, file);
	        userDto.setImageName(fileName);// Assuming you want to set the uploaded file name as imageName
	        UserDto updateduser = this.userService.updateUser(userDto, userId);
	        return new ResponseEntity<>(updateduser, HttpStatus.OK);
	    }
	    
	    //method to serve files
	    @GetMapping(value = "/image/{imageName}",produces = MediaType.IMAGE_JPEG_VALUE)
	    public void downloadImage(
	            @PathVariable("imageName") String imageName,
	            HttpServletResponse response
	    ) throws IOException {
	    	String fileExtension = FilenameUtils.getExtension(imageName).toLowerCase();
	        MediaType mediaType = MediaType.IMAGE_JPEG;  // Default

	        if (fileExtension.equals("png")) {
	            mediaType = MediaType.IMAGE_PNG;
	        } else if (fileExtension.equals("jpg") || fileExtension.equals("jpeg")) {
	            mediaType = MediaType.IMAGE_JPEG;
	        }

	        response.setContentType(mediaType.toString());
	        try (InputStream resource = this.fileService.getResource(path, imageName)) {
	            StreamUtils.copy(resource, response.getOutputStream());
	        }
	    }

}
