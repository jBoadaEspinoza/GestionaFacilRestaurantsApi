package com.api.GestionaFacilRestaurants.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.api.GestionaFacilRestaurants.models.CashRegisterView;
public interface CashRegisterViewRepository extends JpaRepository<CashRegisterView,Long> {
    Page<CashRegisterView> findAllByBusinessIdAndDenominationContainingIgnoreCase(Long businessId,String denomination,Pageable pageable);
    Page<CashRegisterView> findAllByBusinessId(Long businessId,Pageable pageable);
}
