package com.ride_share.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Data
@NoArgsConstructor
public class Rider {
	 @Id
	    @GeneratedValue(strategy = GenerationType.AUTO)
	    private int id;
	 
	 @ManyToOne
	    @JoinColumn(name = "user_id")
	    private User user;

	    private String driver_License; // Driver License No
	    private String Nid_No;
	    private String citizen_No;
	    private String date_Of_Birth;
	    
	    private Double balance;
	    
	    private String license_Image;
	    private String citizen_Front;
	    private String citizen_Back;
	    private String Nid_Img;
	    private String selfieWithIdCard; // Image path
	    @ManyToOne
	    @JoinColumn(name = "category_id")
	    private Category category;
	    
	    //additional
	    private LocalDateTime addedDate;
	    
	    @Column(name = "updated_date")
	    private LocalDateTime updatedDate;

	    private String statusMessage; // General message field for any status
	    
	    @Enumerated(EnumType.STRING)
	    private RiderStatus status;
	    
	    public enum RiderStatus {
	        PENDING, APPROVED, REJECTED
	    }
	    
}
