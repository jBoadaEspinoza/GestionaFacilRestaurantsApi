package com.api.GestionaFacilRestaurants.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.GestionaFacilRestaurants.responses.ApiResponse;
import com.api.GestionaFacilRestaurants.security.JwtAuthenticationFilter;
import com.api.GestionaFacilRestaurants.services.DocumentPaySerieService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("${api.base.path}/document-pay-series")
public class DocumentPaySerieController {
    @Autowired
    private DocumentPaySerieService documentPaySerieService;
    
    @GetMapping
    public ResponseEntity<?> get(HttpServletRequest request){
        String token = JwtAuthenticationFilter.getTokenFromRequest(request);
        Object response = documentPaySerieService.get(token);
        return ResponseEntity.ok(new ApiResponse(response));
    }
}
