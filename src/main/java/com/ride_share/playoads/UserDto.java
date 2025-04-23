package com.ride_share.playoads;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

	
	private int id;
	
	private Location currentLocation;

	@NotEmpty
	@Size(min = 4, message = "Username must be min of 4 characters !!")
	private String name;

	@Email(message = "Email address is not valid !!")
	@NotEmpty(message = "Email is required !!")
	private String email;
	
	 
	  @Schema(hidden = true)
	 private ManagerAddress managerAddress;
	 
	@NotEmpty(message="Mobile num is required !!")
	 private String mobileNo;
	
	@NotEmpty
	@Size(min = 3, max = 10, message = "Password must be min of 3 chars and max of 10 chars !!")
	private String password;

	private String imageName;
	  @Schema(hidden = true)
	private String  otp;
	 private String balance;
	private String branch_Name;
	
	 private UserMode modes;
	    
	    public enum UserMode {
	        RIDER,PESSENGER
	    }
	   
	//--------------------------
	  //@Column(name = "date_of_registration")
     // private LocalDateTime dateOfRegistration;

      //private LocalDateTime date_Of_Role_Changed;
      
     // private LocalDateTime otpValidUntil;
	
      private String date_of_Birth;
      
      private String mode;
      
	private Set<RoleDto> roles = new HashSet<>();
	private Set<VehicleDto> vehicles; 
	
	
	@JsonIgnore
	public String getPassword() {
		return this.password;
	}
	
	@JsonProperty
	public void setPassword(String password) {
		this.password=password;
	}

}
