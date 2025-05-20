package com.api.GestionaFacilRestaurants.requests;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ComandaDetailRequest {
    private Long menuItemId;
    private double unitPricePen;
    private Long quantity;
    @Builder.Default
    private String suggestion=null;
}
