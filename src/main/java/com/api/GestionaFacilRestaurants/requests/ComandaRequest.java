package com.api.GestionaFacilRestaurants.requests;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ComandaRequest {
    private Long tableId;
    @Builder.Default
    private Long customerId=null;
}
