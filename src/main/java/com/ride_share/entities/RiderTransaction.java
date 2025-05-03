package com.ride_share.entities;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiderTransaction {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private int transactionId;

	    @ManyToOne
	    @JoinColumn(name = "rider_id")
	    private Rider rider;

	    private Double amount; // Positive for credit, Negative for debit
	    private String type;   // "CREDIT" or "DEBIT"
	    private String reason; // e.g. "Ride Deduction", "Top-up"
	    private LocalDateTime dateTime;
}
