package com.api.GestionaFacilRestaurants.repositories;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.api.GestionaFacilRestaurants.models.DocumentPayTypeHasPersonalDocumentType;
public interface DocumentPayTypeHasPersonalDocumentTypeRepository extends JpaRepository<DocumentPayTypeHasPersonalDocumentType,Long>{
    boolean existsByDocumentPayTypeIdAndPersonalDocumentTypeId(Long documentPayTypeId, Long personalDocumentTypeId);
    List<DocumentPayTypeHasPersonalDocumentType> findAllByDocumentPayTypeId(Long documentPaySerieId);
}
