package com.api.GestionaFacilRestaurants.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.api.GestionaFacilRestaurants.models.CashRegisterOpening;

public interface CashRegisterOpeningRepository extends JpaRepository<CashRegisterOpening,Long>{
    
}
