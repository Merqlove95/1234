package com.example.demo.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlantEntryWithNumberOfItems {
    PlantInventoryEntry entry;
    Long count;
}