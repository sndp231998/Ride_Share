package com.ride_share.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyContact {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer econtactId;
	
	private String name;
	private String mobile;
	
	   @ManyToOne
	    @JoinColumn(name = "user_id")
	    private User user;
	
}
