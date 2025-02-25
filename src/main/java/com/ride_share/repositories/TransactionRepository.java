package com.ride_share.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ride_share.entities.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByRefId(String refId);
}
