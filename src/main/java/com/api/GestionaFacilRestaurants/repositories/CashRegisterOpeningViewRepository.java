package com.api.GestionaFacilRestaurants.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.api.GestionaFacilRestaurants.models.CashRegisterOpeningView;
public interface CashRegisterOpeningViewRepository extends JpaRepository<CashRegisterOpeningView,Long>{
    List<CashRegisterOpeningView> findAllByBusinessId(Long businessId);
    Optional<CashRegisterOpeningView> findByIdAndBusinessId(Long id,Long businessId);
    Optional<CashRegisterOpeningView> findByCashRegisterId(Long cashRegisterId);
}
