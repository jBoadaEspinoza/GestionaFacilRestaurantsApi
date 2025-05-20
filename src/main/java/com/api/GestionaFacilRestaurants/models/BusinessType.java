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
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="establecimientos_tipos")
public class BusinessType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="denominacion_es")
    private String denominationEs;

    @Column(name="denominacion_en")
    private String denominationEn;

    @Column(name="denominacion_plural_es")
    private String denominationPluralEs;

    @Column(name="denominacion_plural_en")
    private String denominationPluralEn;

    @ManyToOne
    @JoinColumn(name="modulo_id")
    private Module module;
    
}
