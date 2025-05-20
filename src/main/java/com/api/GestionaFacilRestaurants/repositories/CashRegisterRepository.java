package com.api.GestionaFacilRestaurants.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.GestionaFacilRestaurants.models.CashRegister;

public interface CashRegisterRepository extends JpaRepository<CashRegister,Long>{

}
