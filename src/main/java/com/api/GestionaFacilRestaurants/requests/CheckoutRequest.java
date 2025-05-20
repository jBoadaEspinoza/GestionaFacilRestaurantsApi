package com.api.GestionaFacilRestaurants.requests;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckoutRequest {
    private Long cashOpeningId;
    private Long documentPaySerieId;
    private Long paymentMethodId;
    @Builder.Default
    private Long customerId=null;
    @Builder.Default
    private Double tipAmount=0.0;
    @Builder.Default
    private String note=null;
    @Builder.Default
    private String issueDate=null;

}
