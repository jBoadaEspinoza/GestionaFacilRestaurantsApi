package com.api.GestionaFacilRestaurants.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuItemRequest {
    private Long id=null;
    private String denomination;
    private String description=null;
    private String barcode=null;
    private Long categoryId;
    private Long presentationId;
    private Double pricePen;
    private String imageUrl=null;
    private Long dispatchAreaId;
    private boolean active=true;
}
