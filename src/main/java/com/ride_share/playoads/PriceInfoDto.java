package com.ride_share.playoads;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceInfoDto {
    private double totalKm;
    private double generatedPrice;
    private String state;
    private double time;
}

