package com.ride_share.entities;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;


import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Data
@NoArgsConstructor
public class ForgetPassword {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer fid;
	
	  private String phnum;
	private LocalDateTime date;
	
	private String otp;
	private String newPassword;
	
	 @ManyToOne
	    private User user;
}