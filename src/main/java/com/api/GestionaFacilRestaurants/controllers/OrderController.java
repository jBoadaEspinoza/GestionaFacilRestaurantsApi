package com.api.GestionaFacilRestaurants.controllers;

import java.util.Map;

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

import com.api.GestionaFacilRestaurants.requests.CheckoutRequest;
import com.api.GestionaFacilRestaurants.requests.ComandaDetailRequest;
import com.api.GestionaFacilRestaurants.requests.ComandaRequest;
import com.api.GestionaFacilRestaurants.responses.ApiResponse;
import com.api.GestionaFacilRestaurants.security.JwtAuthenticationFilter;
import com.api.GestionaFacilRestaurants.services.OrderService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("${api.base.path}/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;
    
    @GetMapping
    public ResponseEntity<?> getAll(
        HttpServletRequest request,
        @RequestParam(value="closed" , required = false, defaultValue = "true") Boolean closed,
        @RequestParam(value="skip", required=false, defaultValue = "0") Integer skip,
        @RequestParam(value="limit", required=false, defaultValue = "10") Integer limit,
        @RequestParam(value="sort", required=false, defaultValue = "DESC") String sort
    ){
        Sort.Direction sortDirection;
        sortDirection = Sort.Direction.valueOf(sort.toUpperCase());        
        String token = JwtAuthenticationFilter.getTokenFromRequest(request);

        Object response = orderService.get(token,closed,skip, limit, sortDirection);
        return ResponseEntity.ok(new ApiResponse(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(HttpServletRequest request, @PathVariable("id") Long id){
        String token = JwtAuthenticationFilter.getTokenFromRequest(request);
        Object response = orderService.getById(token, id);
        return ResponseEntity.ok(new ApiResponse(response));
    }

    @PostMapping
    public ResponseEntity<?> insert(HttpServletRequest request, @RequestBody ComandaRequest input){
        String token = JwtAuthenticationFilter.getTokenFromRequest(request);
        Object response = orderService.insert(token,input);
        return ResponseEntity.ok(new ApiResponse(response));
    }

    @PostMapping("/{id}/checkout")
    public ResponseEntity<?> checkout(HttpServletRequest request,@PathVariable("id") Long id,@RequestBody CheckoutRequest input){
        String token = JwtAuthenticationFilter.getTokenFromRequest(request);
        Object response = orderService.checkout(token,id,input);
        return ResponseEntity.ok(new ApiResponse(response));
    }

    @PostMapping("/{id}/updateTipAmount")
    public ResponseEntity<?> updateTipAmount(HttpServletRequest request,@PathVariable("id") Long id,@RequestBody Map<String,String> input){
        String token = JwtAuthenticationFilter.getTokenFromRequest(request);
        Object response = orderService.updateTipAmount(token,id,input);
        return ResponseEntity.ok(new ApiResponse(response));
    }

    @GetMapping("/{id}/beforeCheckout")
    public ResponseEntity<?> beforeCheckout(HttpServletRequest request,@PathVariable("id") Long id){
        String token = JwtAuthenticationFilter.getTokenFromRequest(request);
        Object response = orderService.beforeCheckout(token,id);
        return ResponseEntity.ok(new ApiResponse(response));
    }

    @PostMapping("/{id}/addItemDetail")
    public ResponseEntity<?> addItemDetail(HttpServletRequest request, @PathVariable("id") Long id, @RequestBody ComandaDetailRequest comanda){
        String token = JwtAuthenticationFilter.getTokenFromRequest(request);
        Object response = orderService.addItemDetail(token,id,comanda);
        return ResponseEntity.ok(new ApiResponse(response));
    }

    @PostMapping("/{id}/delete")
    public ResponseEntity<?> delete(HttpServletRequest request, @PathVariable("id") Long id){
        String token = JwtAuthenticationFilter.getTokenFromRequest(request);
        Object response = orderService.delete(token,id);
        return ResponseEntity.ok(new ApiResponse(response));
    }
}
