package com.ride_share.playoads;

import java.time.LocalDateTime;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForgetPasswordDto {

	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer fid;
	
	 private String phnum;
	private LocalDateTime  date;
	
	private String otp;
	private String newPassword;

	    private UserDto user;
}
