package com.ride_share.playoads;



import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManagerDto {
	    private int managerId;
           private String provision;
		    private String localLevel;
		    private String district;
		    private String wardnumber;
    //@JsonIgnore
	    private String mobileNo;
	   private UserDto user;
	    private BranchDto branch;
}
