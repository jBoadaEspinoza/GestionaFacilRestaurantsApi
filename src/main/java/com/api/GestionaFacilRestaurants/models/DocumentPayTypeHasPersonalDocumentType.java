package com.api.GestionaFacilRestaurants.models;

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
@Table(name="comprobantes_de_pago_tipos_has_personas_documentos_tipos")
public class DocumentPayTypeHasPersonalDocumentType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="documento_de_pago_tipo_id")
    private DocumentPayType documentPayType; 

    @ManyToOne
    @JoinColumn(name="persona_documento_tipo_id")
    private PersonalDocumentType personalDocumentType;
}
