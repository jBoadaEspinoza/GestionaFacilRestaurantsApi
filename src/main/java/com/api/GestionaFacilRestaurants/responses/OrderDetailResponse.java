package com.api.GestionaFacilRestaurants.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailResponse {
    private Long id;
    private MenuItemResponse menuItem;
    private Long quantity;
    private Double unitPrice;
    private String suggestion;
}
