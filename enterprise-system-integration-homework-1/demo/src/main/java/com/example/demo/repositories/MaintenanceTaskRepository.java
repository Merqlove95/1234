
package com.example.demo.repositories;

import com.example.demo.models.MaintenanceTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MaintenanceTaskRepository extends JpaRepository<MaintenanceTask, Long> {

    @Query(value = "select task.reservation.plant.plantInfo " +
            "from MaintenanceTask task " +
            "where task in " +
            "(select oneTask from MaintenanceTask as oneTask where (oneTask.schedule.startDate not between :begin and :now )) ")
    List<MaintenanceTask> checkMonths (@Param("begin") LocalDate beginningOfPeriod, @Param("now") LocalDate now);
}