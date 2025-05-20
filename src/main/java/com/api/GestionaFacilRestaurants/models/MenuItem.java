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
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="productos")
public class MenuItem {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="codigo_de_barra")
    private String barcode;

    @Column(name="denominacion")
    private String denomination;

    @Column(name="descripcion")
    private String description;

    @ManyToOne
    @JoinColumn(name="categoria_id")
    private Category Category;

    @ManyToOne
    @JoinColumn(name="presentacion_id")
    private Presentation presentation;

    @Column(name="imagen_url")
    private String urlImage;

    @ManyToOne
    @JoinColumn(name="area_despacho_id")
    private DispatchArea dispatchArea;
    
    @ManyToOne
    @JoinColumn(name="establecimiento_id",nullable = false)
    private Business business;
    
    @Column(name="activo")
    private boolean active;

    @Column(name="precio_pen")
    private double pricePen;

}   
