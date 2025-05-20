package com.api.GestionaFacilRestaurants.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.api.GestionaFacilRestaurants.models.Category;

public interface CategoryRepository extends JpaRepository<Category,Long>{
   Page<Category> findAllByBusinessId(Long businessId,Pageable pageable);
   Page<Category> findAllByBusinessIdAndDenominationPerGroupContainingIgnoreCase(Long businessId, String denominationPerGroup, Pageable pageable);
}
