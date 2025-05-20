package com.api.GestionaFacilRestaurants.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.GestionaFacilRestaurants.models.PostalCode;

public interface PostalCodeRepository extends JpaRepository<PostalCode,Long> {
    List<PostalCode> findAllByOrderByCountryNameEsAsc();
}
