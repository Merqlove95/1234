package com.example.demo.repositories;

import com.example.demo.models.PlantEntryWithNumberOfItems;
import com.example.demo.models.PlantInventoryEntry;
import com.example.demo.models.PlantInventoryItem;
import org.apache.tomcat.jni.Local;

import java.time.LocalDate;
import java.util.List;

public interface CustomInventoryRepository {
    List<PlantInventoryEntry> findAvailablePlants(String name, LocalDate startDate, LocalDate endDate);
    List<PlantEntryWithNumberOfItems> findAvailablePlantsPair(String name, LocalDate startDate, LocalDate endDate);
    List<PlantInventoryItem> findAvailablePlantsItem(String name, LocalDate startDate, LocalDate endDate);
}
