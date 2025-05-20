package com.api.GestionaFacilRestaurants.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TableRequest {
    @Builder.Default
    private Long id = null;
    private String denomination;
    @Builder.Default
    private boolean active=true;
}
