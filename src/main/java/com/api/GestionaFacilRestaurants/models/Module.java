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
@Table(name="modulos")
public class Module {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="denominacion_singular_es")
    private String denominationSingularEs;

    @Column(name="denominacion_plural_es")
    private String denominationPluralEs;

    @Column(name="activo")
    private boolean active;

    @Column(name="plan_mensual")
    private Double mounthlyPlan;

}
