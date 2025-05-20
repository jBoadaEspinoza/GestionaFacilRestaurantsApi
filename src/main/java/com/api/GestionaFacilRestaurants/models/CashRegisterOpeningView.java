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
@Table(name="vista_cajas_aperturadas")
public class CashRegisterOpeningView {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="fecha_apertura")
    private String dateOpening;

    @Column(name="caja_id")
    private Long cashRegisterId;

    @Column(name="caja_denominacion")
    private String cashRegisterDenomination;

    @Column(name="establecimiento_id")
    private Long businessId;

    @Column(name="persona_id")
    private Long personId;

    @Column(name="persona_nombres")
    private String personSurnames;

    @Column(name="persona_apellidos")
    private String personLastnames;
}
