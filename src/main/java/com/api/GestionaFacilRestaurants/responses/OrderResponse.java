package com.api.GestionaFacilRestaurants.responses;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {
    private Long id;
    @JsonInclude(Include.NON_NULL)
    private Long waiterId;
    @JsonInclude(Include.NON_NULL)
    private Long customerId;
    private String issueDate;
    @JsonInclude(Include.NON_NULL)
    private String closingDate;
    @JsonInclude(Include.NON_NULL)
    private boolean closing;
    @JsonInclude(Include.NON_NULL)
    private String fileName;
    @JsonInclude(Include.NON_NULL)
    private String url;
    @JsonInclude(Include.NON_NULL)
    private Long numbering;
    @JsonInclude(Include.NON_NULL)
    private double tipAmount;
    @JsonInclude(Include.NON_NULL)
    private Object metadata;
    @JsonInclude(Include.NON_NULL)
    private List<OrderDetailResponse> orderDetails;
}
