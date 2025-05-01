package com.ride_share;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.ride_share.service.impl.PricingServiceImpl;

@Component
public class Test implements CommandLineRunner{

	@Autowired
	PricingServiceImpl p;
	@Override
	public void run(String... args) throws Exception {
		//p.deductBalanceBasedOnRides();
		
	}

}
