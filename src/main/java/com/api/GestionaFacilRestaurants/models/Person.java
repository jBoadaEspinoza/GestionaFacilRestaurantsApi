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
@Table(name="personas")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="documento_tipo_id")
    private PersonalDocumentType personalDocumentType;

    @Column(name="documento_numero")
    private String documentNumber;

    @Column(name="nombres")
    private String surnames;

    @Column(name="apellidos")
    private String lastnames;

    @Column(name="fecha_nacimiento")
    private String birthDate;

    @Column(name="sexo")
    private String sex;

    @Column(name="pais_residencia_id")
   private Long residenceCountryId;

   @Column(name="pais_nacimiento_id")
   private Long birthCountryId;

   @Column(name="celular_numero")
   private String cellphoneNumber;

   @ManyToOne
   @JoinColumn(name="celular_postal_id")
   private PostalCode postalCode;

   @Column(name="correo_electronico")
   private String email;

   @Column(name="direccion")
   private String address;

   @Column(name="razon_social")
   private String razonSocial;
}
