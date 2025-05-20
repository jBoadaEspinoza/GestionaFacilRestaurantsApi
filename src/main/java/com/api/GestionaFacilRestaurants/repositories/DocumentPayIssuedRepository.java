package com.api.GestionaFacilRestaurants.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.api.GestionaFacilRestaurants.models.DocumentPayIssued;

public interface DocumentPayIssuedRepository extends JpaRepository<DocumentPayIssued,Long>, JpaSpecificationExecutor<DocumentPayIssued>{
    @Query("SELECT d FROM DocumentPayIssued d WHERE d.documentPaySerie.id = :documentPaySerieId ORDER BY d.numbering DESC LIMIT 1")
    Optional<DocumentPayIssued> findLatestByOrderIdAndDocumentPaySerieId(@Param("documentPaySerieId") Long documentPaySerieId);

    @Query("SELECT d FROM DocumentPayIssued d WHERE d.order.id = :orderId ORDER BY d.issueDate DESC LIMIT 1")
    Optional<DocumentPayIssued> findByOrderId(@Param("orderId") Long orderId);

    
}
