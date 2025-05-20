package com.api.GestionaFacilRestaurants.responses;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryResponse {
    private Long id;
    private String denominationPerUnit;
    private String denominationPerGroup;
    private String url;
}
