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
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="cajas")
public class CashRegister {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="denominacion")
    private String denomination;

    @Column(name="fecha_de_registro")
    private String creationDate;

    @Column(name="establecimiento_id")
    private Long businessId;

    @Column(name="activo")
    private boolean active;
    
}
