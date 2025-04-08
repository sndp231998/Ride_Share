package com.ride_share.playoads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PriceInfoDto {
    private int totalKm;
    private double generatedPrice;
    private String state;
}

