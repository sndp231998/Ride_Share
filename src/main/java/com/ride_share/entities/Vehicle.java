package com.ride_share.entities;

import javax.persistence.Entity;
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
public class Vehicle {
	    @Id
	    @GeneratedValue(strategy = GenerationType.AUTO)
	    private int id;

	    private String vehicle_type; // Car, Rickshaw, Moto
	    
	    private String vechicle_Brand; //suzuki,honda,...
	    
	    private String vechicle_Number; // Vehicle number//plate number
	    
	    
	    private String vechicle_Img; //gadi ko photo
	    
	    private String bill_book1;//1,2 prista ko photo ek choti mai
	    private String bill_book2; //9 or 10 prista ko poto
	    
	    private String production_Year;  ///
	    
	    
//	    @ManyToOne
//	    @JoinColumn(name = "rider_id")
//	    private Rider rider;
	    
		@ManyToOne
		private User user;
	    
	    @ManyToOne
		@JoinColumn(name = "category_id")
		private Category category;

//	    private String vehicleType; // Car, Rickshaw, Moto
//	    private String vehicleNumber; // Vehicle Number
//	    private String vehicleModel; // Vehicle Model
}
