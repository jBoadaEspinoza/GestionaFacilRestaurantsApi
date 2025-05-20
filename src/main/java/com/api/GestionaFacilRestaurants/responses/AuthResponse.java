package com.api.GestionaFacilRestaurants.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private boolean success=true;
    private String token;
    public AuthResponse(String token) {
        this.token = token;
    }
}
