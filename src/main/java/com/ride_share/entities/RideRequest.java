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



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideRequest {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int rideRequestId;
	
	
	private double actualPrice=0.0;
	
	
//	 @Embedded
//	    private Destination_Coordinates destination;
	private double d_latitude;
    private double d_longitude;
	private String d_Name;
    private double s_latitude;
    private double s_longitude;
	private String s_Name;
	private double replacePessengerPrice=0.0;
	private double total_Km;
	private LocalDateTime addedDate;
	@ManyToOne
    @JoinColumn(name = "user_id")// The passenger who created the request pessenger Id
    private User user;
	
	 @ManyToOne
	    @JoinColumn(name = "category_id")
	    private Category category;
	
	 @OneToMany(mappedBy = "rideRequest", cascade = CascadeType.ALL)
		private Set<RiderApprovalRequest> riderApprovalRequests=new HashSet<>();
	
	
	 @Enumerated(EnumType.STRING)
	    private RideStatus status;
	    
	    public enum RideStatus {
	        PENDING,PESSENGER_APPROVED, REJECTED
	    }
	
//------------------------------------------------------
//	@Column(name="approved_user_Id")// after pessenger choosed rider, storing userId
//	private String ridebookedId;

}
