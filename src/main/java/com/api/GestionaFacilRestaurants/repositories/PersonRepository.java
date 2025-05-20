package com.api.GestionaFacilRestaurants.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.api.GestionaFacilRestaurants.models.Person;

public interface PersonRepository extends JpaRepository<Person,Long>{
    Optional<Person> findByDocumentNumberAndPersonalDocumentTypeDenominationShort(String documentNumber,String documentTypeShort);
}
