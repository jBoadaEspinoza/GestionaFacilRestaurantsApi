package com.api.GestionaFacilRestaurants.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CashRegisterOpeningRequest {
    private Long cashRegisterId;
    private Long cashierId;
    private String currency = "PEN";
    private Double amount = 0.0;
}
