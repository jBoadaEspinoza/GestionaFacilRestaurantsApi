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

import com.api.GestionaFacilRestaurants.requests.UserRequest;
import com.api.GestionaFacilRestaurants.responses.ApiResponse;
import com.api.GestionaFacilRestaurants.security.JwtAuthenticationFilter;
import com.api.GestionaFacilRestaurants.services.UserService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("${api.base.path}/users")
public class UserController {
    @Autowired
    private UserService userService;
    
    @GetMapping(value="/me")
    public ResponseEntity<?> getMe(HttpServletRequest request){
        String token = JwtAuthenticationFilter.getTokenFromRequest(request);
        Object response = userService.getMe(token);
        return ResponseEntity.ok(new ApiResponse(response));
    }

    @GetMapping
    public ResponseEntity<?> get(
        HttpServletRequest request,
        @RequestParam(value="denomination", required = false) String denomination,
        @RequestParam(value="role_id",required = false) Long role_id,
        @RequestParam(value="skip", required=false, defaultValue = "0") Integer skip,
        @RequestParam(value="limit", required=false, defaultValue = "10") Integer limit,
        @RequestParam(value="sort", required=false, defaultValue = "DESC") String sort){
        
            Sort.Direction sortDirection;
            sortDirection = Sort.Direction.valueOf(sort.toUpperCase());        
            String token = JwtAuthenticationFilter.getTokenFromRequest(request);
            Object response = userService.get(token,denomination,role_id,skip,limit,sortDirection);
            return ResponseEntity.ok(new ApiResponse(response));
    }

    @PostMapping
    public ResponseEntity<?> create(HttpServletRequest request,@RequestBody UserRequest input){
        String token = JwtAuthenticationFilter.getTokenFromRequest(request);
        Object response = userService.save(token,input);
        return ResponseEntity.ok(new ApiResponse(response));
    }

}
