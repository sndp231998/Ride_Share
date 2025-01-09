package com.ride_share.entities;

import java.util.ArrayList;
import java.util.List;

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

    @Column(nullable = false, unique = true)
    private String name;

   // private String location; // Optional: Can include address details if needed.

//   @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//   private List<User> users = new ArrayList<>();
}
