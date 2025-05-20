package com.api.GestionaFacilRestaurants.repositories;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.api.GestionaFacilRestaurants.models.Order;

public interface OrderRepository extends JpaRepository<Order,Long>, JpaSpecificationExecutor<Order>{
    Optional<Order> findByIdAndBusinessId(Long OrderId,Long businessId);
    Optional<Order> findTopByClosedFalseAndBusinessIdAndMetadataContaining(Long businessId,String metadataPattern);
    Optional<Order> findTopByBusinessIdOrderByNumberingDesc(Long businessId);
    Optional<Order> findByBusinessIdAndNumbering(Long businessId,Long numbering);
}
