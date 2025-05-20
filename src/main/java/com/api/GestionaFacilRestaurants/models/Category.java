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
@Table(name="categorias")
public class Category {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="denominacion_por_unidad")
    private String denominationPerUnit;

    @Column(name="denominacion_por_grupo")
    private String denominationPerGroup;

    @Column(name="imagen_url")
    private String url;

    @ManyToOne
    @JoinColumn(name="establecimiento_id",nullable = false)
    private Business business;
}
