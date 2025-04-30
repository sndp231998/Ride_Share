package com.ride_share.playoads;



import lombok.Data;

@Data
public class ManagerDto {
	    private int managerId;

	 private String managerProvision;
	    private String managerLocalLevel;
	    private String managerDistrict;
	    private String manager_wardnumber;
	    private String mobileNo;
	   private UserDto user;
	    private BranchDto branch;
}
