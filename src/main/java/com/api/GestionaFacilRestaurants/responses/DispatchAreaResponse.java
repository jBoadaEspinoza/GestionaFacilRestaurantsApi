package com.api.GestionaFacilRestaurants.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DispatchAreaResponse {
    private Long id;
    private String denominationSingularEs;
    private String denominationPluralEs;
    private boolean active;
}
