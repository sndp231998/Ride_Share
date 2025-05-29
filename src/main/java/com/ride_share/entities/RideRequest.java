package com.ride_share.entities;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "rideRequestId")

public class RideRequest {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int rideRequestId;
	private double actualPrice=0.0;
	private double d_latitude;
    private double d_longitude;
	private String d_Name;
    private double s_latitude;
    private double s_longitude;
	private String s_Name;
	private double replacePessengerPrice=0.0;
	private double total_Km;
	private int total_min;
	private LocalDateTime addedDate;
	@ManyToOne
    @JoinColumn(name = "user_id")// The passenger who created the request pessenger Id
    private User user;
	
	 @ManyToOne
	    @JoinColumn(name = "category_id")
	    private Category category;
	
	 @OneToMany(mappedBy = "rideRequest", cascade = CascadeType.ALL)
	 @JsonIgnore 
    private Set<RiderApprovalRequest> riderApprovalRequests=new HashSet<>();
	
	
	 @Enumerated(EnumType.STRING)
	    private RideStatus status;
	    
	    public enum RideStatus {
	        PENDING,PESSENGER_APPROVED, REJECTED ,RIDE_COMPLETE,PICKUP
	    }
	
//------------------------------------------------------
	@Column(name="approved_user_Id")// after pessenger choosed rider, storing userId
	private Integer ridebookedId;

}
