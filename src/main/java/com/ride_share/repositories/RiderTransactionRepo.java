package com.ride_share.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ride_share.entities.RiderTransaction;

public interface RiderTransactionRepo  extends JpaRepository<RiderTransaction, Integer>{

	List<RiderTransaction> findByRiderIdOrderByDateTimeDesc(Integer riderId);
}
