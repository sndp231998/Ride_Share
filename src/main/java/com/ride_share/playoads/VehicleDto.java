package com.ride_share.playoads;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VehicleDto {
    
    private int id;

    private String vehicle_type; // Car, Rickshaw, Moto
    
    private String vechicle_Brand; //suzuki,honda,...
    
    private String vechicle_Number; // Vehicle number//plate number
    
    
    private String vechicle_Img; //gadi ko photo
    
    private String bill_book1;//1,2 prista ko photo ek choti mai
    private String bill_book2; //9 or 10 prista ko poto
    
    private String production_Year;  ///

    private CategoryDto category;

	private UserDto user;
}
