package com.api.GestionaFacilRestaurants.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.GestionaFacilRestaurants.requests.LoginRequest;
import com.api.GestionaFacilRestaurants.responses.ApiResponse;
import com.api.GestionaFacilRestaurants.services.AuthService;

@RestController
@RequestMapping("${api.base.path}/auth")
public class AuthController {
    @Autowired
    private AuthService authService;
    @PostMapping(value="/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest input){
        Object response = authService.login(input);
        return ResponseEntity.ok(new ApiResponse(response));
    }
}
