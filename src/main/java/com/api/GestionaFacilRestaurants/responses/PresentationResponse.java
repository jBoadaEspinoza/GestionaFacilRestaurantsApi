package com.api.GestionaFacilRestaurants.responses;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PresentationResponse {
    private Long id;
    private String denomination;
}
