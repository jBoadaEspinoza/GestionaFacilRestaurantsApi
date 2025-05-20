package com.api.GestionaFacilRestaurants.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.GestionaFacilRestaurants.models.MenuItem;

public interface MenuItemRepository extends JpaRepository<MenuItem,Long> {

}
