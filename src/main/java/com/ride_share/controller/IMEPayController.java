package com.ride_share.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ride_share.playoads.PaymentRequestDTO;
import com.ride_share.playoads.PaymentResponseDTO;
import com.ride_share.service.IMEPayService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/imepay")
@RequiredArgsConstructor
public class IMEPayController {

    private final IMEPayService imePayService;

    @PostMapping("/initiate")
    public ResponseEntity<PaymentResponseDTO> initiatePayment(@RequestBody PaymentRequestDTO requestDTO) {
        return ResponseEntity.ok(imePayService.initiatePayment(requestDTO));
    }

    @GetMapping("/verify/{refId}")
    public ResponseEntity<String> verifyTransaction(@PathVariable String refId) {
        imePayService.verifyTransaction(refId);
        return ResponseEntity.ok("Transaction Verified");
    }
}