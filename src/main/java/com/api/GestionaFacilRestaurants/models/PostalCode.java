package com.api.GestionaFacilRestaurants.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="codigos_telefonicos")
public class PostalCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="code")
    private String numbering;

    @Column(name="code2")
    private String code2;

    @Column(name="nombre_es")
    private String countryNameEs;

    @Column(name="nombre_en")
    private String countryNameEn;

    @Column(name="nombre_fr")
    private String countryNameFr;

    @Column(name="continente_id")
    private Long continentId;

}
