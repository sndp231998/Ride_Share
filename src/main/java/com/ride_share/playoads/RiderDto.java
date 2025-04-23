package com.ride_share.playoads;


import java.time.LocalDateTime;

import javax.persistence.Column;

import com.ride_share.entities.Category;
import com.ride_share.entities.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiderDto {

	    private int id;
	 
	    private String driver_License; // Driver License
	    
	    private String selfieWithIdCard; // Image path
	    
	    private String date_Of_Birth;
	    
	    private String balance;
	    
	    private LocalDateTime addedDate;
	    @Column(name = "updated_date")
	    private LocalDateTime updatedDate;

	    private String statusMessage; // General message field for any status
	    
private RiderStatus status;
	    
	    public enum RiderStatus {
	        PENDING, APPROVED, REJECTED
	    }
	    
	    private UserDto user;
	    private CategoryDto category;
}
