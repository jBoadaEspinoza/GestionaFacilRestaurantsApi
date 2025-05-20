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
@Table(name="usuarios")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="nombre")
    private String name;

    @Column(name="clave_acceso")
    private String pass;

    @ManyToOne
    @JoinColumn(name="persona_id")
    private Person person;

    @ManyToOne
    @JoinColumn(name="rol_id",nullable = false)
    private Role role;

    @Column(name="establecimiento_id")
    private Long businessId;

    @Column(name="foto_url")
    private String photoUrl;

    @Column(name="activo")
    private boolean active;

    
}
