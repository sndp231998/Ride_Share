package com.ride_share.service;

import com.ride_share.playoads.PaymentRequestDTO;
import com.ride_share.playoads.PaymentResponseDTO;


public interface IMEPayService {
	 PaymentResponseDTO initiatePayment(PaymentRequestDTO requestDTO);
	 
	    void verifyTransaction(String refId);
}
