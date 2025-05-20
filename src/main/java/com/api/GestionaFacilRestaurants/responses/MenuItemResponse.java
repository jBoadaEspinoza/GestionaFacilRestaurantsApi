package com.api.GestionaFacilRestaurants.responses;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MenuItemResponse {
    private Long id;
    @Builder.Default
    private String barcode=null;
    private String denomination;
    private String denominationFull;
    @Builder.Default
    private String description=null;
    @JsonInclude(Include.NON_NULL)
    private CategoryResponse category;
    @JsonInclude(Include.NON_NULL)
    private PresentationResponse presentation;
    @JsonInclude(Include.NON_NULL)
    private DispatchAreaResponse dispatchArea;
    @JsonInclude(Include.NON_NULL)
    private String imageUrl;
    @JsonInclude(Include.NON_NULL)
    private Double pricePen;
    @JsonInclude(Include.NON_NULL)
    private boolean active; 
}
