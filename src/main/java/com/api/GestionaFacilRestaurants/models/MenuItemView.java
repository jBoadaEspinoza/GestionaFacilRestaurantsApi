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
@Table(name="vista_productos")
@Builder
public class MenuItemView {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    @Column(name="codigo_de_barra")
    private String barcode=null;

    @Column(name="full_denominacion")
    private String fullDenomination;
    
    @Column(name="denominacion")
    private String denomination;

    @Builder.Default
    @Column(name="descripcion")
    private String description=null;

    @ManyToOne
    @JoinColumn(name="categoria_id")
    private Category category;

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

    @Column(name="total_pedido")
    private Long totalPedido;
}   
