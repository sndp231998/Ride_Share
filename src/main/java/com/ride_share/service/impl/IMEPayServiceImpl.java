package com.ride_share.service.impl;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ride_share.entities.Transaction;
import com.ride_share.playoads.PaymentRequestDTO;
import com.ride_share.playoads.PaymentResponseDTO;
import com.ride_share.repositories.TransactionRepository;
import com.ride_share.service.IMEPayService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IMEPayServiceImpl implements IMEPayService {

    private final TransactionRepository transactionRepository;
    private final RestTemplate restTemplate;
    private final String API_URL = "https://stg.imepay.com.np:8555/api/Web/GetToken";
    private final String VERIFY_URL = "https://stg.imepay.com.np:8555/api/Web/Confirm";
    private final String API_USER = "demoimepay";
    private final String API_PASSWORD = "IMEPay@123";
    private final String MODULE = "DEMOIMEP";

    @Override
    public PaymentResponseDTO initiatePayment(PaymentRequestDTO requestDTO) {
        HttpHeaders headers = new HttpHeaders();
        String auth = Base64.getEncoder().encodeToString((API_USER + ":" + API_PASSWORD).getBytes());
        headers.set("Authorization", "Basic " + auth);
        headers.set("Module", MODULE);
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<PaymentRequestDTO> entity = new HttpEntity<>(requestDTO, headers);
        ResponseEntity<PaymentResponseDTO> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, PaymentResponseDTO.class);
        
        if (response.getBody() != null && response.getBody().getResponseCode() == 0) {
            Transaction transaction = new Transaction(null, requestDTO.getMerchantCode(), requestDTO.getRefId(), requestDTO.getAmount(), response.getBody().getTokenId(), null, null, 2, LocalDateTime.now(), null);
            transactionRepository.save(transaction);
        }
        return response.getBody();
    }

    @Override
    public void verifyTransaction(String refId) {
        Optional<Transaction> transactionOpt = transactionRepository.findByRefId(refId);
        if (transactionOpt.isPresent()) {
            Transaction transaction = transactionOpt.get();
            HttpHeaders headers = new HttpHeaders();
            String auth = Base64.getEncoder().encodeToString((API_USER + ":" + API_PASSWORD).getBytes());
            headers.set("Authorization", "Basic " + auth);
            headers.set("Module", MODULE);
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, String> request = Map.of(
                "MerchantCode", transaction.getMerchantCode(),
                "RefId", transaction.getRefId(),
                "TokenId", transaction.getTokenId()
            );
            
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);
            
            // **Fix: Use ParameterizedTypeReference**
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                VERIFY_URL, 
                HttpMethod.POST, 
                entity, 
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            
            if (response.getBody() != null && response.getBody().get("responseCode").equals(0)) {
                transaction.setTransactionId(response.getBody().get("TransactionId").toString());
                transaction.setMsisdn(response.getBody().get("Msisdn").toString());
                transaction.setImeTxnStatus(0);
                transaction.setResponseDate(LocalDateTime.now());
                transactionRepository.save(transaction);
            }
        }
    }
}