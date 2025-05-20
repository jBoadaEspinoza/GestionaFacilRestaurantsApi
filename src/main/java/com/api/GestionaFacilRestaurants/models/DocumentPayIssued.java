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
@Table(name="documentos_de_pago_emitidos")
public class DocumentPayIssued {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 

    @Column(name="fecha_emision")
    private String issueDate;

    @ManyToOne
    @JoinColumn(name="apertura_caja_id")
    private CashRegisterOpening cashRegisterOpening;

    @ManyToOne
    @JoinColumn(name="pedido_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name="serie_id")
    private DocumentPaySerie documentPaySerie;

    @Column(name="numeracion")
    private Long numbering;

    @ManyToOne
    @JoinColumn(name="modalidades_de_pago_id")
    private PaymentMethod paymentMethod;

    @ManyToOne
    @JoinColumn(name="cliente_id")
    private Person customer;

    @Column(name="moneda_id")
    @Builder.Default
    private String currency="PEN";

    @Column(name="monto_total")
    private Double amountTotal;

    @Column(name="monto_propina")
    private Double amountTip;

    @Column(name="metadata")
    private String metadata;

    @Column(name="anulado")
    private boolean cancelled;
}
