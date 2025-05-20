package com.api.GestionaFacilRestaurants.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.api.GestionaFacilRestaurants.models.OrderDetail;

public interface OrderDetailRepository extends JpaRepository<OrderDetail,Long> {
    List<OrderDetail> findAllByOrderId(Long orderId);
}
