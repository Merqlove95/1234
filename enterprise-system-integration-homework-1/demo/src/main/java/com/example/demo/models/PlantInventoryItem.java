package com.example.demo.models;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class PlantInventoryItem {

    @Id
    @GeneratedValue
    Long id;

    String serialNumber;

    @Enumerated(EnumType.STRING)
    EquipmenetCondition equipmentCondition;

    @ManyToOne
    PlantInventoryEntry plantInfo;
}