package com.api.GestionaFacilRestaurants.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.GestionaFacilRestaurants.models.Business;

public interface BusinessRepository extends JpaRepository<Business,Long>{
    Optional<Business> findByRuc(Long ruc);
}
