package com.api.GestionaFacilRestaurants.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ErrorResponse {
    @Builder.Default
    private boolean success=false;
    private String code;
    private String message;
    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }
    
}
