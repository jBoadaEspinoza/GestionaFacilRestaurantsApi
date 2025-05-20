package com.api.GestionaFacilRestaurants.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name="documentos_de_pago_series")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentPaySerie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="documento_de_pago_tipo_id")
    private DocumentPayType documentPayType;

    @Column(name="numeracion")
    private String numbering;

    @Column(name="establecimiento_id")
    private Long businessId;

    @Column(name="activo")
    private boolean active;
}
