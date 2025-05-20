package com.api.GestionaFacilRestaurants.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.api.GestionaFacilRestaurants.models.Authorization;

public interface AuthRepository extends JpaRepository<Authorization,Long>, JpaSpecificationExecutor<Authorization>{
    Optional<Authorization> findByUserIdAndUserModel(Long userId,String userModel);
    Optional<Authorization> findByBusinessRucAndUserNameAndRoleIdAndModuleIdAndActive(
            Long businessRuc, String userName,Long roleId, Long moduleId, Boolean active);
}
