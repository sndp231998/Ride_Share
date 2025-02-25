package com.ride_share.playoads;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {  // Add "public"
    private Integer responseCode;
    private String tokenId;
    private Double amount;
    private String refId;
}
