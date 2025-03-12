package com.ride_share.playoads;


import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.ride_share.entities.Branch;

import lombok.Data;
@Data
@Embeddable
public class ManagerAddress {
    private String managerProvision;
    private String managerLocalLevel;
    private String managerDistrict;
    private String manager_wardnumber;
    @ManyToOne
    @JoinColumn(name = "branch_id")
    private Branch branch;
    //private Long branchId;
}
