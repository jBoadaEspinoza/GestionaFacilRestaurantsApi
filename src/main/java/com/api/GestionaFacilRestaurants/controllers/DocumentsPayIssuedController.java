package com.api.GestionaFacilRestaurants.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.api.GestionaFacilRestaurants.responses.ApiResponse;
import com.api.GestionaFacilRestaurants.security.JwtAuthenticationFilter;
import com.api.GestionaFacilRestaurants.services.DocumentsPayIssuedService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("${api.base.path}/documents-pay-issued")
public class DocumentsPayIssuedController {
    @Autowired
    private DocumentsPayIssuedService documentsPayIssuedService;

    @GetMapping
    public ResponseEntity<?> getOrders(
            HttpServletRequest request,
            @RequestParam(value="skip", required=false, defaultValue = "0") Integer skip,
            @RequestParam(value="limit", required=false, defaultValue = "10") Integer limit,
            @RequestParam(value="sort", required=false, defaultValue = "DESC") String sort,
            @RequestParam(value="paymentMethodId", required=false) Long paymentMethodId,
            @RequestParam(value="cashRegisterId", required=false) Long cashRegisterId,
            @RequestParam(value="userId", required=false) Long userId) {
       
        Sort.Direction sortDirection;
        sortDirection = Sort.Direction.valueOf(sort.toUpperCase());        
        String token = JwtAuthenticationFilter.getTokenFromRequest(request);
        Object response = documentsPayIssuedService.getAll(token,skip, limit, sortDirection, userId,paymentMethodId);
        return ResponseEntity.ok(new ApiResponse(response));
    }
}
