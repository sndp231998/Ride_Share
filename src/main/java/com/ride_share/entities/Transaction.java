package com.ride_share.entities;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String merchantCode;
    
    @Column(nullable = false, unique = true)
    private String refId;
    
    @Column(nullable = false)
    private Double amount;
    
    @Column(nullable = false)
    private String tokenId;
    
    private String transactionId;
    private String msisdn;
    private Integer imeTxnStatus;
    private LocalDateTime requestDate;
    private LocalDateTime responseDate;
}