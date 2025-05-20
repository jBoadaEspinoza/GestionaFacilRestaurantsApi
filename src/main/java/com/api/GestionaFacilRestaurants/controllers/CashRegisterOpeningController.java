package com.api.GestionaFacilRestaurants.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.GestionaFacilRestaurants.requests.CashRegisterClosingRequest;
import com.api.GestionaFacilRestaurants.requests.CashRegisterOpeningRequest;
import com.api.GestionaFacilRestaurants.responses.ApiResponse;
import com.api.GestionaFacilRestaurants.security.JwtAuthenticationFilter;
import com.api.GestionaFacilRestaurants.services.CashRegisterOpeningService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("${api.base.path}/cash-opening")
public class CashRegisterOpeningController {
    @Autowired
    private CashRegisterOpeningService cashRegisterOpeningService;

    @GetMapping
    public ResponseEntity<?> get(HttpServletRequest request){
        String token = JwtAuthenticationFilter.getTokenFromRequest(request);
        Object response = cashRegisterOpeningService.get(token);
        return ResponseEntity.ok(new ApiResponse(response));
    }

    @PostMapping("/open")
    public ResponseEntity<?> open(HttpServletRequest request, @RequestBody CashRegisterOpeningRequest input){
        String token = JwtAuthenticationFilter.getTokenFromRequest(request);
        Object response = cashRegisterOpeningService.open(token,input);
        return ResponseEntity.ok(new ApiResponse(response));
    }

    @PostMapping("/close")
    public ResponseEntity<?> close(HttpServletRequest request,@RequestBody CashRegisterClosingRequest input){
        String token = JwtAuthenticationFilter.getTokenFromRequest(request);
        Object response = cashRegisterOpeningService.close(token,input);
        return ResponseEntity.ok(new ApiResponse(response));
    } 
}
