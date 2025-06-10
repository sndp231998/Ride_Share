package com.ride_share.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ride_share.entities.Rider;
import com.ride_share.entities.RiderTransaction;
import com.ride_share.playoads.RiderDto;
import com.ride_share.playoads.RiderTransactionDto;
import com.ride_share.repositories.RiderTransactionRepo;

@Service
public class RiderTransactionServiceImpl {

	@Autowired
    private  RiderTransactionRepo riderTransactionRepo;

    public List<RiderTransactionDto> getStatementForRider(Integer riderId) {
        List<RiderTransaction> transactions = riderTransactionRepo.findByRider_IdOrderByDateTimeDesc(riderId);

        return transactions.stream().map(this::convertToDto).toList();
    }

    private RiderTransactionDto convertToDto(RiderTransaction transaction) {
        RiderTransactionDto dto = new RiderTransactionDto();
        dto.setTransactionId(transaction.getTransactionId());
        dto.setRiderId(transaction.getRider().getId()); // ✅ only ID
        dto.setAmount(transaction.getAmount());
        dto.setType(transaction.getType());
        dto.setReason(transaction.getReason());
        dto.setDateTime(transaction.getDateTime());
        return dto;
    }
}
