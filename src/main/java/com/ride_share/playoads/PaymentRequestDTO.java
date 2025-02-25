package com.ride_share.playoads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDTO { 
    private String merchantCode;
    private Double amount;
    private String refId;
}
