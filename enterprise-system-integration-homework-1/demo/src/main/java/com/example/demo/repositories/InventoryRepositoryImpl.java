package com.example.demo.repositories;

import com.example.demo.models.PlantEntryWithNumberOfItems;
import com.example.demo.models.PlantInventoryEntry;
import com.example.demo.models.PlantInventoryItem;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

public class InventoryRepositoryImpl implements CustomInventoryRepository {
    @Autowired
    EntityManager em;

    @Override
    public List<PlantInventoryEntry> findAvailablePlants(String name, LocalDate startDate, LocalDate endDate) {
        return em.createQuery("select i.plantInfo from PlantInventoryItem i where lower(i.plantInfo.name) like ?1 and i not in " +
                        "(select r.plant from PlantReservation r where ?2 < r.schedule.endDate and ?3 > r.schedule.startDate)"
                , PlantInventoryEntry.class)
                .setParameter(1, name)
                .setParameter(2, startDate)
                .setParameter(3, endDate)
                .getResultList();
    }

    public List<PlantInventoryItem> findAvailablePlantsItem(String name, LocalDate startDate, LocalDate endDate) {
//        JPA doesn't support datediff as not all databases support it. Another choice would be to use native query
        LocalDate nowPlusThreeWeeks = LocalDate.now().plusWeeks(3);
        return em.createQuery(
                "select p from PlantInventoryItem p " +
                        "where LOWER(p.plantInfo.name) like concat('%', ?1, '%') " +
                        "and (p.equipmentCondition = 'SERVICEABLE' " +
                        "or p.equipmentCondition <> 'UNSERVICEABLE_CONDEMNED' " +
                        "and ?2  >= ?4) " +
                        "and p not in " +
                        "(select r.plant from PlantReservation r " +
                        "where ?2 < r.schedule.endDate and ?3 > r.schedule.startDate)",
                PlantInventoryItem.class)
                .setParameter(1, name)
                .setParameter(2, startDate)
                .setParameter(3, endDate)
                .setParameter(4, nowPlusThreeWeeks)
                .getResultList();
    }

    public List<PlantEntryWithNumberOfItems> findAvailablePlantsPair(String name, LocalDate startDate, LocalDate endDate) {
//        JPA doesn't support datediff as not all databases support it. Another choice would be to use native query
        LocalDate nowPlusThreeWeeks = LocalDate.now().plusWeeks(3);
        return em.createQuery(
                "select new com.example.demo.models.PlantEntryWithNumberOfItems(ent, count(item)) " +
                        "from PlantInventoryItem item " +
                        "join item.plantInfo as ent where item in (" +
                        "select p.plantInfo from PlantInventoryItem p " +
                        "where LOWER(p.plantInfo.name) like concat('%', ?1, '%') " +
                        "and (p.equipmentCondition = 'SERVICEABLE' " +
                        "or p.equipmentCondition <> 'UNSERVICEABLE_CONDEMNED' " +
                        "and ?2  >= ?4) " +
                        "and p not in " +
                        "(select r.plant from PlantReservation r " +
                        "where ?2 < r.schedule.endDate and ?3 > r.schedule.startDate)) " +
                        "group by ent.name",
                PlantEntryWithNumberOfItems.class)
                .setParameter(1, name)
                .setParameter(2, startDate)
                .setParameter(3, endDate)
                .setParameter(4, nowPlusThreeWeeks)
                .getResultList();
    }
}
