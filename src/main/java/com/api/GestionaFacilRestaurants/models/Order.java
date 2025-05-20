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
@Table(name="pedidos")
@Builder

public class Order {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name="mozo_id")
    private Long waiterId;

    @ManyToOne
    @JoinColumn(name="cliente_id")
    private Person customer;

    @Column(name="fecha_hora_emision")
    private String issueDate;

    @Builder.Default
    @Column(name="fecha_hora_cierre")
    private String closingDate=null;

    @Builder.Default
    @Column(name="cerrado")
    private boolean closed=false;
    
    @Column(name="referencia_a_bd")
    private String metadata;
    
    @ManyToOne
    @JoinColumn(name="establecimiento_id")
    private Business business;

    @Column(name="numeracion")
    private Long numbering;

}
