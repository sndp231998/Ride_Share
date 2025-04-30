package com.ride_share.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.ride_share.playoads.UserDto;

import lombok.Data;

@Data
@Entity
public class Manager {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private int managerId;

	 private String managerProvision;
	    private String managerLocalLevel;
	    private String managerDistrict;
	    private String manager_wardnumber;
	    @ManyToOne
	    @JoinColumn(name = "branch_id")
	    private Branch branch;
	    @ManyToOne
	    @JoinColumn(name = "user_id")
	    private User user;
}
