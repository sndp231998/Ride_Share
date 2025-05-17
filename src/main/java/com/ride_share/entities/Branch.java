package com.ride_share.entities;


import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Branch {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NotBlank(message = "Branch code is required")
    private String branchCode;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Branch name is required")
    private String name;

    // Address Information of branch
    @NotBlank(message = "Province is required")
    private String province;

    @NotBlank(message = "District is required")
    private String district;

    @NotBlank(message = "Local level is required")
    private String localLevel;

    @Min(value = 1, message = "Ward number must be at least 1")
    private int wardNumber;

    // Contact details of branch
    @Pattern(regexp = "^98[0-9]{8}$", message = "Invalid Nepali phone number")
    private String phoneNo;

    @Email(message = "Invalid email address")
    private String email;


}
