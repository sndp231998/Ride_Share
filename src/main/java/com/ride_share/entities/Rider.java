package com.ride_share.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Data
@NoArgsConstructor
public class Rider {
	 @Id
	    @GeneratedValue(strategy = GenerationType.AUTO)
	    private int id;
	 
	 //1. BAsic Info //user table bata 
	 //2. Driver lic 
	 //3. selfi with Id
	 //4. Vehicle info
	 
	 
	    @OneToOne
	    @JoinColumn(name = "user_id")
	    private User user;

	    private String driver_License; // Driver License
	    
	    private String selfieWithIdCard; // Image path
	    
	    
	   
	 
}
