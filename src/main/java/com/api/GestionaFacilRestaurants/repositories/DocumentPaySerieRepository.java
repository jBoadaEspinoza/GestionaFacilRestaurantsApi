package com.api.GestionaFacilRestaurants.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.api.GestionaFacilRestaurants.models.DocumentPaySerie;

public interface DocumentPaySerieRepository extends JpaRepository<DocumentPaySerie,Long> {
    List<DocumentPaySerie> findAllByBusinessIdOrderByIdDesc(Long businessId);
    Optional<DocumentPaySerie> findByIdAndBusinessId(Long id,Long businessId);
}
