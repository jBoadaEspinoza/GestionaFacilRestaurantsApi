package com.api.GestionaFacilRestaurants.repositories;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.api.GestionaFacilRestaurants.models.TableView;

public interface TableViewRepository extends JpaRepository<TableView,Long> {
    Page<TableView> findAllByBusinessIdAndActive(Long businessId,Boolean active, Pageable pageable);
    Page<TableView> findAllByDenominationAndBusinessIdAndActive(String denomination,Long businessId,boolean active,Pageable pageable);

    @Query("SELECT COUNT(p) FROM TableView p WHERE p.businessId = :businessId")
    Long countByBusinessId(@Param("businessId") Long businessId);

    
}
