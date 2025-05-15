package com.ride_share.entities;


import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Branch {

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

//   private String baseFare ;
//  private String perKmRate ;
    
    
   // private String location; // Optional: Can include address details if needed.

//   @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//   private List<User> users = new ArrayList<>();
}
