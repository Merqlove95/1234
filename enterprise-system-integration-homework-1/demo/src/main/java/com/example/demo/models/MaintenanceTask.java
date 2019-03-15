package com.example.demo.models;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
public class MaintenanceTask {
    @Id
    @GeneratedValue
    Long id;

    String description;

    @Column(precision = 8, scale = 2)
    BigDecimal price;

    @Enumerated(EnumType.STRING)
    TypeOfWork typeOfWork;

    @Embedded
    BusinessPeriod schedule;

    @ManyToOne
    PlantReservation reservation;
}
