package com.api.GestionaFacilRestaurants.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.api.GestionaFacilRestaurants.models.DispatchArea;

public interface DispatchAreaRepository extends JpaRepository<DispatchArea,Long>{
   Page<DispatchArea> findAllByDenominationPluralEsContainingIgnoreCase(String denomination, Pageable pageable);
}
