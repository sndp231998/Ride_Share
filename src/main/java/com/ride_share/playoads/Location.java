package com.ride_share.playoads;


import java.time.LocalDateTime;

import javax.persistence.Embeddable;


import lombok.Data;


@Embeddable
@Data
public class Location {

    private Double latitude;
    private Double longitude;
    private LocalDateTime timestamp;
 

}
