package com.api.GestionaFacilRestaurants.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.GestionaFacilRestaurants.models.Role;

public interface RoleRepository extends JpaRepository<Role,Long>{
    List<Role> findAllByModuleId(Long moduleId);
}
