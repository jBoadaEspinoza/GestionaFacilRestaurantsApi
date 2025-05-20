package com.api.GestionaFacilRestaurants.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.api.GestionaFacilRestaurants.requests.CashRegisterRequest;
import com.api.GestionaFacilRestaurants.responses.ApiResponse;
import com.api.GestionaFacilRestaurants.security.JwtAuthenticationFilter;
import com.api.GestionaFacilRestaurants.services.CashRegisterService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("${api.base.path}/cash-registers")
public class CashRegisterController {
    @Autowired
    private CashRegisterService cashRegisterService;
    @GetMapping
    public ResponseEntity<?> get(
        HttpServletRequest request,
        @RequestParam(value="denomination", required = false) String denomination,
        @RequestParam(value="skip", required=false, defaultValue = "0") Integer skip,
        @RequestParam(value="limit", required=false, defaultValue = "10") Integer limit,
        @RequestParam(value="sort", required=false, defaultValue = "DESC") String sort){
            Sort.Direction sortDirection;
            sortDirection = Sort.Direction.valueOf(sort.toUpperCase());        
            String token = JwtAuthenticationFilter.getTokenFromRequest(request);
            Object response = cashRegisterService.get(token,denomination ,skip, limit, sortDirection);
            return ResponseEntity.ok(new ApiResponse(response));
    }

    @PostMapping
    public ResponseEntity<?> post(HttpServletRequest request,@RequestBody CashRegisterRequest cashRegisterRequest){
        String token = JwtAuthenticationFilter.getTokenFromRequest(request);
        Object response = cashRegisterService.create(token, cashRegisterRequest);
        return ResponseEntity.ok(new ApiResponse(response));
    }

    
}
