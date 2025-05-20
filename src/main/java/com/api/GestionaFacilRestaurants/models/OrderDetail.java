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
@Table(name="pedidos_detalles")
public class OrderDetail {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="pedido_id")
    private Long orderId;

    @ManyToOne
    @JoinColumn(name="producto_id",nullable = false)
    private MenuItem menuItem;

    @Column(name="precio_unitario_pen")
    private Double unitPricePen;

    @Column(name="cantidad")
    private Long quantity;

    @Column(name="sugerencias")
    private String suggestion;

}
