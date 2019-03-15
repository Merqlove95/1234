package com.example.demo.repositories;

import com.example.demo.DemoApplication;
import com.example.demo.models.BusinessPeriod;
import com.example.demo.models.PlantInventoryEntry;
import com.example.demo.models.PlantInventoryItem;
import com.example.demo.models.PlantReservation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DemoApplication.class)
@Sql(scripts="/plants-dataset.sql")
@DirtiesContext(classMode=DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class InventoryRepositoryTest {

    @Autowired
    PlantInventoryEntryRepository plantInventoryEntryRepo;

    @Autowired
    PlantInventoryItemRepository plantInventoryItemRepo;

    @Autowired
    PlantReservationRepository plantReservationRepo;

    @Autowired
    InventoryRepository inventoryRepo;

    @Autowired
    MaintenanceTaskRepository maintenanceTaskRepo;

    @Test
    public void queryPlantCatalog() {
        assertThat(plantInventoryEntryRepo.count()).isEqualTo(14l);
    }

    @Test
    public void queryByName() {
        assertThat(plantInventoryEntryRepo.findByNameContaining("Mini").size()).isEqualTo(2);
        assertThat(plantInventoryEntryRepo.finderMethod("mini").size()).isEqualTo(2);
        assertThat(plantInventoryEntryRepo.finderMethodV2("mini").size()).isEqualTo(2);
    }

    @Test
    public void findAvailableTest() {
        PlantInventoryEntry entry = plantInventoryEntryRepo.findById(1l).orElse(null);
        PlantInventoryItem item = plantInventoryItemRepo.findOneByPlantInfo(entry);

        assertThat(inventoryRepo.findAvailablePlants(entry.getName().toLowerCase(), LocalDate.of(2017,2,20), LocalDate.of(2017,2,25)))
                .contains(entry);

        PlantReservation po = new PlantReservation();
        po.setId(1L);
        po.setPlant(item);
        po.setSchedule(BusinessPeriod.of(LocalDate.of(2017, 2, 20), LocalDate.of(2017, 2, 25)));
        plantReservationRepo.save(po);

        assertThat(inventoryRepo.findAvailablePlants(entry.getName().toLowerCase(), LocalDate.of(2017,2,20), LocalDate.of(2017,2,25)))
                .doesNotContain(entry);
    }

    @Test
    public void checkMonths(){
        LocalDate now = LocalDate.now();
        LocalDate beginning = LocalDate.now().minusMonths(12);
        System.out.println(maintenanceTaskRepo.checkMonths(now, beginning));
        assertThat(maintenanceTaskRepo.checkMonths(beginning, now).size()).isEqualTo(3);
    }
}
