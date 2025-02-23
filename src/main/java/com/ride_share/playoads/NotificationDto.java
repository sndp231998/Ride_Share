package com.ride_share.playoads;

import java.time.LocalDateTime;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;



import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class NotificationDto {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

   
    private boolean isRead = false;
     
    private UserDto user;

    private LocalDateTime createdAt = LocalDateTime.now();
}
