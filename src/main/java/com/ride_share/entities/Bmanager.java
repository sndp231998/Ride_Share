package com.ride_share.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class Bmanager {

	    @Id
	    @GeneratedValue(strategy = GenerationType.AUTO)
	private int mId;
	// Manager Information
    private String managerName;
    private String managerPhone;
    private String managerEmail;
//Address of manger
    private String manager_Province;
    private String manager_District;
    private String manager_LocalLevel;
    private String manager_wardnumber;
    
    private String userId;
    
    @ManyToOne
    @JoinColumn(name = "branch_id")
    private Branch branch;
    
    
}
