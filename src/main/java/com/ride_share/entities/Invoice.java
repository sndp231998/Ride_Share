package com.ride_share.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Invoice {

	 @Id
	    @GeneratedValue(strategy = GenerationType.AUTO)
	    private int invoice_Id;
	 
	 
	    
}
