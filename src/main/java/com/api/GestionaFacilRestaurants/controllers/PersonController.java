package com.api.GestionaFacilRestaurants.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.api.GestionaFacilRestaurants.services.PersonService;
import com.api.GestionaFacilRestaurants.responses.ApiResponse;
import com.api.GestionaFacilRestaurants.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("${api.base.path}/persons")
public class PersonController {
    @Autowired
    private PersonService personService;

    @GetMapping
    public ResponseEntity<?> get(HttpServletRequest request, @RequestParam(value = "documentType" , required = true) String documentType,@RequestParam(value = "documentNumber", required = true) String documentNumber){
        String token = JwtAuthenticationFilter.getTokenFromRequest(request);
        Object response = personService.get(token,documentType,documentNumber);
        return ResponseEntity.ok(new ApiResponse(response));
    }
}
