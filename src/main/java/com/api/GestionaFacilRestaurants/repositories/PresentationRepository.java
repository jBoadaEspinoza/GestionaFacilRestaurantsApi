package com.api.GestionaFacilRestaurants.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.api.GestionaFacilRestaurants.models.Presentation;

public interface PresentationRepository extends JpaRepository<Presentation,Long>{
   Page<Presentation> findAllByBusinessId(Long businessId,Pageable pageable);
   Page<Presentation> findAllByBusinessIdAndDenominationContainingIgnoreCase(Long businessId, String denomination, Pageable pageable);
}
