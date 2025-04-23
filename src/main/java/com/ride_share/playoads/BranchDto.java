package com.ride_share.playoads;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BranchDto {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    
    private String branchCode;
    @Column(nullable = false, unique = true)
    private String name;
    
 // Address Information of branch
    private String province;
    private String district;
    private String localLevel;
    private int wardNumber;
    
    //contact detail of branch
   private String phoneNo;
   private String email;


   private String baseFare ;
  private String perKmRate ;
}
