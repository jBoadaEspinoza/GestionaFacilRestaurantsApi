package com.api.GestionaFacilRestaurants.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.api.GestionaFacilRestaurants.models.PaymentMethod;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod,Long>{
    
}
