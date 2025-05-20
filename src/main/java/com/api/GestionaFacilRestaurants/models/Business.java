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
@Table(name="establecimientos")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Business {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    @Column(name="nombre_comercial")
    private String name;

    @Column(name="razon_social")
    private String razonSocial;

    @Column(name="ruc")
    private Long ruc;
    
    @Column(name="direccion_denominacion")
    private String address;
    
    @Column(name="apisunat_persona_token")
    private String apiSunatPersonaToken;

    @Column(name="apisunat_persona_id")
    private String apiSunatPersonaId;

    @Column(name="nrus")
    private boolean nrus;

    @Column(name="abierto")
    private boolean opened;

    @Column(name="activo")
    private boolean active;

    @ManyToOne
    @JoinColumn(name="tipo_id")
    private BusinessType type;

    
}
