package com.api.GestionaFacilRestaurants.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.GestionaFacilRestaurants.requests.TableRequest;
import com.api.GestionaFacilRestaurants.responses.ApiResponse;
import com.api.GestionaFacilRestaurants.security.JwtAuthenticationFilter;
import com.api.GestionaFacilRestaurants.services.TableService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("${api.base.path}/tables")
public class TableController {
    @Autowired
    private TableService tableService;

    @GetMapping
    public ResponseEntity<?> get(
            HttpServletRequest request,
            @RequestParam(value="denomination", required = false) String denomination,
            @RequestParam(value="skip", required=false, defaultValue = "0") Integer skip,
            @RequestParam(value="limit", required=false, defaultValue = "10") Integer limit,
            @RequestParam(value="sort", required=false, defaultValue = "ASC") String sort,
            @RequestParam(value="active", required=false, defaultValue = "true") boolean active){

        Sort.Direction sortDirection;
        sortDirection = Sort.Direction.valueOf(sort.toUpperCase());        
        String token = JwtAuthenticationFilter.getTokenFromRequest(request);
        Object response= tableService.get(token,denomination,skip,limit,active,sortDirection);
        return ResponseEntity.ok(new ApiResponse(response));
    }

    @PostMapping
    public ResponseEntity<?> post(HttpServletRequest request, @RequestBody TableRequest input){
        String token = JwtAuthenticationFilter.getTokenFromRequest(request);
        Object response = tableService.save(token, input);
        return ResponseEntity.ok(new ApiResponse(response));
    }
    
    @PostMapping("/{id}/change-status")
    public ResponseEntity<?> changeStatus(
            HttpServletRequest request, 
            @PathVariable("id") Long id, 
            @RequestParam(value="active", required = true) boolean active){
        String token = JwtAuthenticationFilter.getTokenFromRequest(request);
        Object response = tableService.changeStatus(token, id, active);
        return ResponseEntity.ok(new ApiResponse(response));
    }
}   
