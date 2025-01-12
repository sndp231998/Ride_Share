package com.ride_share.playoads;

import com.ride_share.entities.User;

import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
public class RiderDto {

	    private int id;
	 
	    private String driver_License; // Driver License
	    
	    private String selfieWithIdCard; // Image path
	    
	    private String date_Of_Birth;
	    
	    private User user;
}
