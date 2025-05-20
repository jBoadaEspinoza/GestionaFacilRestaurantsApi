package com.api.GestionaFacilRestaurants.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryRequest {
    @Builder.Default
    private Long id = null;
    private String denominationPerUnit;
    private String denominationPerGroup;
    @Builder.Default
    private String imageUrl=null;
}
