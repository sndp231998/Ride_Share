package com.ride_share.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ride_share.playoads.BranchDto;
import com.ride_share.playoads.UserDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Manager {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private int managerId;
	
	 private String provision;
	 private String localLevel;
     private String district;
	 private String wardnumber;
	 
	    @ManyToOne
	    @JoinColumn(name = "branch_id")
	    private Branch branch;
	    
	    @ManyToOne
	    @JoinColumn(name = "user_id")
	    private User user;
}
//@Column(nullable=false)