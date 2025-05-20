package com.api.GestionaFacilRestaurants.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.api.GestionaFacilRestaurants.models.User;

public interface UserRepository extends JpaRepository<User,Long> {
     Page<User> findAllByBusinessId(Long businessId,Pageable pageable);
}
