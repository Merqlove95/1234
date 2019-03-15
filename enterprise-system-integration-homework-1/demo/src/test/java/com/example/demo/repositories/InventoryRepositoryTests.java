package com.example.demo.repositories;

import com.example.demo.DemoApplication;
import com.example.demo.models.*;
import org.apache.tomcat.jni.Local;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import sun.applet.Main;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DemoApplication.class)
@Sql(scripts="/tests-dataset.sql")
@DirtiesContext(classMode=DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class InventoryRepositoryTests {

    @Autowired PlantInventoryEntryRepository plantInventoryEntryRepository;
    @Autowired PlantInventoryItemRepository plantInventoryItemRepository;
    @Autowired PlantReservationRepository plantReservationRepository;
    @Autowired InventoryRepository inventoryRepository;
    @Autowired MaintenanceTaskRepository maintenanceTaskRepository;
    @Autowired MaintenancePlanRepository maintenancePlanRepository;
    @Autowired PurchaseOrderRepository purchaseOrderRepository;

    private PlantInventoryItem createPlantInventoryItemFor(PlantInventoryEntry entry, EquipmenetCondition condition) {
        PlantInventoryItem item = new PlantInventoryItem();
        item.setPlantInfo(entry);
        item.setEquipmentCondition(condition);
        return plantInventoryItemRepository.save(item);
    }

    private void createMaintenanceTask(BigDecimal price, TypeOfWork typeOfWork, BusinessPeriod schedule, PlantReservation reservation){
        MaintenanceTask task = new MaintenanceTask();
        task.setPrice(price);
        task.setTypeOfWork(typeOfWork);
        task.setSchedule(schedule);
        task.setReservation(reservation);
        maintenanceTaskRepository.save(task);
    }

    private void createMaintenancePlan(List<MaintenanceTask> tasks, Integer year_of_action, PlantInventoryItem plant) {
        MaintenancePlan maintenancePlan = new MaintenancePlan();
        maintenancePlan.setTasks(tasks);
        maintenancePlan.setYear_of_action(year_of_action);
        maintenancePlan.setPlant(plant);
        maintenancePlanRepository.save(maintenancePlan);
    }

    private void createPlantReservation(BusinessPeriod schedule, PlantInventoryItem item){
        PlantReservation reservation = new PlantReservation();
        reservation.setSchedule(schedule);
        reservation.setPlant(item);
        plantReservationRepository.save(reservation);
    }

    private void createPurchaseOrder(List<PlantReservation> reservations, PlantInventoryEntry plant, LocalDate issueDate, LocalDate paymentSchedule,
                                     BigDecimal total, POStatus status, BusinessPeriod rentalPeriod){
        PurchaseOrder order = new PurchaseOrder();
        order.setReservations(reservations);
        order.setPlant(plant);
        order.setIssueDate(issueDate);
        order.setPaymentSchedule(paymentSchedule);
        order.setTotal(total);
        order.setStatus(status);
        order.setRentalPeriod(rentalPeriod);
        purchaseOrderRepository.save(order);
    }

    private void setupData(){
        String serialNumber = "AAA000";

        // The original dataset has 6 different plant inventory entries
        List<PlantInventoryEntry> excavatorsEntries = plantInventoryEntryRepository.findByNameContaining("excavator");
        assertThat(excavatorsEntries).hasSize(6);

//        creating items---------------------------------------------
        for (PlantInventoryEntry excavator: excavatorsEntries) {
            createPlantInventoryItemFor(excavator, EquipmenetCondition.SERVICEABLE);
        }
        createPlantInventoryItemFor(excavatorsEntries.get(0), EquipmenetCondition.SERVICEABLE);
        createPlantInventoryItemFor(excavatorsEntries.get(1), EquipmenetCondition.UNSERVICEABLE_CONDEMNED);
        createPlantInventoryItemFor(excavatorsEntries.get(2), EquipmenetCondition.UNSERVICEABLE_REPAIRABLE);
        createPlantInventoryItemFor(excavatorsEntries.get(3), EquipmenetCondition.UNSERVICEABLE_INCOMPLETE);

//        creating reservations----------------------------------------
        List<PlantInventoryItem> excavatorsItems = plantInventoryItemRepository.findAll();

        BusinessPeriod reservation1TaskPeriod = BusinessPeriod.of(LocalDate.of(2019,02,22), LocalDate.of(2019,02,27));
        BusinessPeriod reservation2TaskPeriod = BusinessPeriod.of(LocalDate.of(2019,03,02), LocalDate.of(2019,03,8));
        createPlantReservation(reservation1TaskPeriod,excavatorsItems.get(0));
        createPlantReservation(reservation2TaskPeriod,excavatorsItems.get(0));

//        creating tasks-------------------------------------------
        List<PlantReservation> reservations = plantReservationRepository.findAll();
        createMaintenanceTask(BigDecimal.valueOf(1000), TypeOfWork.CORRECTIVE, reservations.get(0).getSchedule(), reservations.get(0));
        createMaintenanceTask(BigDecimal.valueOf(500), TypeOfWork.OPERATIVE, reservations.get(1).getSchedule(), reservations.get(1));

//        creating plan--------------------------------------------
        List<MaintenanceTask> tasks = maintenanceTaskRepository.findAll();
        createMaintenancePlan(tasks, 2019, excavatorsItems.get(0));
    }

    @Test
    public void plantsAvailableForABusinessPeriod() {
        LocalDate from = LocalDate.now();
        LocalDate to = from.plusDays(2);

        setupData();
        List<PlantInventoryEntry> excavatorsEntries = plantInventoryEntryRepository.findByNameContaining("excavator");
        List<PlantInventoryItem> excavatorsItems = plantInventoryItemRepository.findAll();
        List<PlantReservation> reservations = plantReservationRepository.findAll();
        List<MaintenanceTask> tasks = maintenanceTaskRepository.findAll();

        List<PlantInventoryItem> excavatorsFree = inventoryRepository.findAvailablePlantsItem("excavator", from, to);
        assertThat(excavatorsEntries.size() == 6);
        assertThat(excavatorsItems.size() == 10);
        assertThat(excavatorsFree.size() == 6);

        // Let us check that we have exactly 6 different types excavators with a least one available physical equipment for each one of them
//        List<PlantEntryWithNumberOfItems> availableExcavators = inventoryRepository.findAvailablePlantsPair("excavator", from, to);
//        System.out.println(availableExcavators);
//        System.out.println(availableExcavators.size());
    }
}
