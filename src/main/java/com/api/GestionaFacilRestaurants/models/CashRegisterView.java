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
@Table(name="vista_estado_cajas")
@Builder
public class CashRegisterView {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="denominacion")
    private String denomination;

    @Column(name="abierta")
    private boolean opening;

    @Column(name="monto_acumulado")
    private Double accumulatedAmount;

    @Column(name="establecimiento_id")
    private Long businessId;

    @Column(name="activo")
    private boolean active;

}
