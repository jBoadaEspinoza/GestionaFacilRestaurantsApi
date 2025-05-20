package com.api.GestionaFacilRestaurants.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.api.GestionaFacilRestaurants.responses.ApiResponse;
import com.api.GestionaFacilRestaurants.security.JwtAuthenticationFilter;
import com.api.GestionaFacilRestaurants.services.PostalCodeService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("${api.base.path}/postal-codes")
public class PostalCodeController {
    @Autowired
    private PostalCodeService postalCodeService;

    @GetMapping
    public ResponseEntity<?> get(
        HttpServletRequest request,
        @RequestParam(value="skip", required=false, defaultValue = "0") Integer skip,
        @RequestParam(value="limit", required=false, defaultValue = "10") Integer limit,
        @RequestParam(value="sort", required=false, defaultValue = "ASC") String sort){

            Sort.Direction sortDirection;
            sortDirection = Sort.Direction.valueOf(sort.toUpperCase());   
            String token = JwtAuthenticationFilter.getTokenFromRequest(request);
            Object response = postalCodeService.get(token,skip,limit,sortDirection);
            return ResponseEntity.ok(new ApiResponse(response));

    }
}
