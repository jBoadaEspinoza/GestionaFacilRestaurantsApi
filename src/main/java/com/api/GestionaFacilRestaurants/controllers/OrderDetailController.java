package com.api.GestionaFacilRestaurants.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.api.GestionaFacilRestaurants.responses.ApiResponse;
import com.api.GestionaFacilRestaurants.security.JwtAuthenticationFilter;
import com.api.GestionaFacilRestaurants.services.OrderDetailService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("${api.base.path}/order-details")
public class OrderDetailController {
    @Autowired
    private OrderDetailService orderDetailService;

    @PostMapping("/{id}/updateQuantity")
    public ResponseEntity<?> updateQuantity(HttpServletRequest request, @PathVariable("id") Long id, @RequestParam Long q){
        String token = JwtAuthenticationFilter.getTokenFromRequest(request);
        Object response = orderDetailService.updateOrderDetailQuantity(token,id, q);
        return ResponseEntity.ok(new ApiResponse(response));
    }

    @PostMapping("/{id}/updateUnitPrice")
    public ResponseEntity<?> updateUnitPrice(HttpServletRequest request, @PathVariable("id") Long id, @RequestParam Double price){
        String token = JwtAuthenticationFilter.getTokenFromRequest(request);
        Object response = orderDetailService.updateUnitPrice(token,id, price);
        return ResponseEntity.ok(new ApiResponse(response));
    }

    @PostMapping("/{id}/deleteItem")
    public ResponseEntity<?> deleteItem(HttpServletRequest request,@PathVariable("id") Long id){
        String token = JwtAuthenticationFilter.getTokenFromRequest(request);
        Object response = orderDetailService.deleteItem(token, id);
        return ResponseEntity.ok(new ApiResponse(response));
    }
}
