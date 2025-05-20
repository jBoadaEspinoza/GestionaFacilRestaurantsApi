package com.api.GestionaFacilRestaurants.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.api.GestionaFacilRestaurants.models.MenuItemView;
import java.util.Optional;

public interface MenuItemViewRepository extends JpaRepository<MenuItemView,Long>,JpaSpecificationExecutor<MenuItemView> {
    Optional<MenuItemView> findByIdAndBusinessId(Long id,Long businessid);
    @Query("SELECT COUNT(p) FROM MenuItemView p WHERE p.business.id = :businessId")
    Long countByBusinessId(@Param("businessId") Long businessId);
}
