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

import com.api.GestionaFacilRestaurants.requests.PresentationRequest;
import com.api.GestionaFacilRestaurants.responses.ApiResponse;
import com.api.GestionaFacilRestaurants.security.JwtAuthenticationFilter;
import com.api.GestionaFacilRestaurants.services.PresentationService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("${api.base.path}/presentations")
public class PresentationController {
    @Autowired
    private PresentationService presentationService;

    @GetMapping
    public ResponseEntity<?> get(
            HttpServletRequest request,
            @RequestParam(value="denomination", required = false) String denomination,
            @RequestParam(value="skip", required=false, defaultValue = "0") Integer skip,
            @RequestParam(value="limit", required=false, defaultValue = "10") Integer limit,
            @RequestParam(value="sort", required=false, defaultValue = "ASC") String sort
    ){
        Sort.Direction sortDirection;
            sortDirection = Sort.Direction.valueOf(sort.toUpperCase());        
            String token = JwtAuthenticationFilter.getTokenFromRequest(request);

           if(denomination!=null && !denomination.isEmpty()){
                Object response = presentationService.getByDenomination(token, denomination ,skip, limit, sortDirection);
                return ResponseEntity.ok(new ApiResponse(response));
            }else{
                Object response = presentationService.get(token, skip, limit, sortDirection);
                return ResponseEntity.ok(new ApiResponse(response));
            }
    }

    @PostMapping
    public ResponseEntity<?> save(HttpServletRequest request,@RequestBody PresentationRequest input){
        String token = JwtAuthenticationFilter.getTokenFromRequest(request);
        Object response = presentationService.save(token,input);
        return ResponseEntity.ok(new ApiResponse(response));
    }
}
