package com.bus_ticket.playoads;

import lombok.Data;

@Data
public class JwtAuthResponse {

private String token;
	
	private UserDto user;
}
