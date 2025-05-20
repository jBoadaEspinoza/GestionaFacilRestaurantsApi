package com.api.GestionaFacilRestaurants.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.api.GestionaFacilRestaurants.responses.ApiResponse;
import com.api.GestionaFacilRestaurants.security.JwtAuthenticationFilter;
import com.api.GestionaFacilRestaurants.services.PaymentMethodService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("${api.base.path}/payment-methods")
public class PaymentMethodController {
    @Autowired
    private PaymentMethodService paymentMethodService;

    @GetMapping
    public ResponseEntity<?> get(HttpServletRequest request){
        String token = JwtAuthenticationFilter.getTokenFromRequest(request);
        Object response = paymentMethodService.get(token);
        return ResponseEntity.ok(new ApiResponse(response));
    }
}
