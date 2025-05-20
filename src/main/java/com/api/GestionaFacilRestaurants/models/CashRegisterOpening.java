package com.api.GestionaFacilRestaurants.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="apertura_caja")
public class CashRegisterOpening {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name="fecha_inicio")
    private String openingDate;

    @Column(name="fecha_cierre")
    private String closingDate;

    @Column(name="caja_id")
    private Long cashRegisterId;

    @Column(name="usuario_id")
    private Long cashierId;

}
