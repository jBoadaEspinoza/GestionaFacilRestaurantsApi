package com.api.GestionaFacilRestaurants.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name="vista_estado_mesas")
@AllArgsConstructor
@NoArgsConstructor
public class TableView {
    @Id
    private Long id;

    @Column(name="denominacion")
    private String denomination;

    @Column(name="establecimiento_id")
    private Long businessId;

    @Column(name="ocupado")
    private boolean occupied;

    @Column(name="orden_id_asociado")
    private Long orderId;

    @Column(name="activo")
    private boolean active;
}
