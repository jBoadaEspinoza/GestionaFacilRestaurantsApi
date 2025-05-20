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
@Table(name="apertura_caja_items")
public class CashRegisterOpeningItems {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="apertura_caja_id")
    private Long cashRegisterOpeningId;

    @Column(name="item_descripcion")
    private String itemDescription;

    @Column(name="fecha_creacion")
    private String creationDate;

    @Column(name="moneda_id")
    @Builder.Default
    private String currency="PEN";

    @Column(name="monto")
    private Double amount;

    @Column(name="tipo")
    @Builder.Default
    private String type="input";

    @Column(name="establecimiento_id")
    private Long businessId;

    @Column(name="metadata")
    private String metadata;
}
