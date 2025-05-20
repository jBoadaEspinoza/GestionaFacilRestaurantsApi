package com.api.GestionaFacilRestaurants.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.GestionaFacilRestaurants.models.PersonalDocumentType;

public interface PersonalDocumentTypeRepository extends JpaRepository<PersonalDocumentType,Long> {
    Optional<PersonalDocumentType> findByDenominationShort(String denominationShort);
}
