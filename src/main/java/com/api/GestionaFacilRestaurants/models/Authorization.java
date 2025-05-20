package com.api.GestionaFacilRestaurants.models;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="vista_usuarios")
public class Authorization implements UserDetails {
    @Id
    private Long id;

    @Column(name = "usuario_id")
    private Long userId;

    @Column(name = "usuario_modelo")
    private String userModel;

    @Column(name = "usuario_nombre")
    private String userName;

    @Column(name = "usuario_clave")
    private String password;

    @Column(name = "usuario_propietario_id")
    private Long userOwnerId;

    @Column(name = "usuario_propietario_nombres")
    private String userOwnerFirstName;

    @Column(name = "usuario_propietario_apellidos")
    private String userOwnerLastName;

    @Column(name = "usuario_propietario_fullname")
    private String userOwnerFullname;

    @Column(name = "usuario_propietario_img")
    private String userOwnerImg;

    @Column(name = "rol_id")
    private Long roleId;

    @Column(name = "rol_nombre_es")
    private String roleNameEs;

    @Column(name = "establecimiento_ruc")
    private Long businessRuc;

    @Column(name = "establecimiento_nombre")
    private String businessName;

    @Column(name = "establecimiento_nrus")
    private boolean businessNrus;

    @Column(name = "establecimiento_abierto")
    private boolean businessOpened;

    @Column(name = "establecimiento_activo")
    private boolean businessActive;

    @Column(name = "establecimiento_tipo_id")
    private Long businessTypeId;

    @Column(name = "establecimiento_tipo_denominacion_singular_es")
    private String businessTypeSingularNameEs;

    @Column(name = "establecimiento_tipo_denominacion_singular_en")
    private String businessTypeSingularNameEn;

    @Column(name = "establecimiento_tipo_denominacion_plural_es")
    private String businessTypePluralNameEs;

    @Column(name = "establecimiento_tipo_denominacion_plural_en")
    private String businessTypePluralNameEn;

    @Column(name = "modulo_id")
    private Long moduleId;

    @Column(name = "modulo_nombre_singular_es")
    private String moduleSingularNameEs;

    @Column(name = "modulo_nombre_plural_es")
    private String modulePluralNameEs;

    @Column(name = "activo")
    private boolean active;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
