package com.ride_share.playoads;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class RiderTransactionDto {

	 
	    private int transactionId;

	    private Integer riderId; 

	    private Double amount; // Positive for credit, Negative for debit
	    private String type;   // "CREDIT" or "DEBIT"
	    private String reason; // e.g. "Ride Deduction", "Top-up"
	    private LocalDateTime dateTime;
}
