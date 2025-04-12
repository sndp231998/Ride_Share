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
public class OtpRequest {
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Integer Id;
	
    private String mobileNo;
    private String otp;
   private  LocalDateTime OtpValidUntil;
   @ManyToOne
   private User user;
}
