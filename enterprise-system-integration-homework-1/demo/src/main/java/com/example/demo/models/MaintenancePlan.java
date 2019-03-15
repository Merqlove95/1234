package com.example.demo.models;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
public class MaintenancePlan {
    @Id
    @GeneratedValue
    Long id;

    @OneToMany(cascade={CascadeType.MERGE})
    List<MaintenanceTask> tasks;

    Integer year_of_action;

    @ManyToOne
    PlantInventoryItem plant;
}
