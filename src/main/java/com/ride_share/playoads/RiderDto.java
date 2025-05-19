package com.ride_share.playoads;


import java.time.LocalDateTime;

import javax.persistence.Column;

import com.ride_share.entities.Category;
import com.ride_share.entities.User;
import com.ride_share.entities.Vehicle;

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
	 

	    private String driver_License; // Driver License No
	    private String nid_No;
	    private String citizen_No;
	    private String date_Of_Birth;
	    
	    private Double balance;
	    
	    private String license_Image;       //img path
	    private String citizen_Front;       //img path
	    private String citizen_Back;      //img path
	    private String Nid_Img;          //img path
	    private String selfieWithIdCard; // Image path
	    
	    private LocalDateTime addedDate;
	    @Column(name = "updated_date")
	    private LocalDateTime updatedDate;

	    private String statusMessage; // General message field for any status
	    
private RiderStatus status;
	    
	    public enum RiderStatus {
	        PENDING, APPROVED, REJECTED
	    }
	    private VehicleDto vehicle;
	    private UserDto user;
	    private CategoryDto category;
}
