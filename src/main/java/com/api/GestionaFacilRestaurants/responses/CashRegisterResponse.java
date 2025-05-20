package com.api.GestionaFacilRestaurants.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CashRegisterResponse {
    private Long id;
    private String denomination;
    private boolean opening;
    private Double accumulatedAmount;
    private boolean active;
}
