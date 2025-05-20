package com.api.GestionaFacilRestaurants.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.api.GestionaFacilRestaurants.models.CashRegisterOpeningItems;

public interface CashRegisterOpeningItemsRepository extends JpaRepository<CashRegisterOpeningItems,Long>{
    List<CashRegisterOpeningItems> findAllByCashRegisterOpeningId(Long cashRegisterOpeningId);
}
