package com.ride_share.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
public class Rider {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Driver license is required")
    private String driver_License;

    
    private String nid_No;

    @NotBlank(message = "Citizenship number is required")
    private String citizen_No;

    @NotBlank(message = "Date of birth is required")
    private String date_Of_Birth;

    @DecimalMin(value = "0.0", message = "Balance must not be negative")
    private Double balance;

    // Image fields
    private String license_Image;
    private String citizen_Front;
    private String citizen_Back;
    private String Nid_Img;
    private String selfieWithIdCard;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private LocalDateTime addedDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    private String statusMessage;

    @Enumerated(EnumType.STRING)
    private RiderStatus status;

    public enum RiderStatus {
        PENDING, APPROVED, REJECTED
    }
}

