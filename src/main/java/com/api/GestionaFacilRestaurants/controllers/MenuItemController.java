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

import com.api.GestionaFacilRestaurants.requests.MenuItemRequest;
import com.api.GestionaFacilRestaurants.responses.ApiResponse;
import com.api.GestionaFacilRestaurants.security.JwtAuthenticationFilter;
import com.api.GestionaFacilRestaurants.services.MenuItemService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("${api.base.path}/menu-items")
public class MenuItemController {

    @Autowired
    private MenuItemService menuItemService;

    @GetMapping
    public ResponseEntity<?> get( HttpServletRequest request,
            @RequestParam(value="denomination", required = false) String denomination,
            @RequestParam(value="category_id", required = false) Long categoryId,
            @RequestParam(value="skip", required=false, defaultValue = "0") Integer skip,
            @RequestParam(value="limit", required=false, defaultValue = "10") Integer limit,
            @RequestParam(value="sort", required=false, defaultValue = "DESC") String sort
        ){

            Sort.Direction sortDirection;
            sortDirection = Sort.Direction.valueOf(sort.toUpperCase());        
            String token = JwtAuthenticationFilter.getTokenFromRequest(request);

            Object response = menuItemService.get(token, denomination, categoryId ,skip, limit, sortDirection);
            return ResponseEntity.ok(new ApiResponse(response));
    }

    @PostMapping
    public ResponseEntity<?> save(HttpServletRequest request,@RequestBody MenuItemRequest input){
        String token = JwtAuthenticationFilter.getTokenFromRequest(request);
        Object response = menuItemService.save(token,input);
        return ResponseEntity.ok(new ApiResponse(response));
    }

    @PostMapping("/{id}/rename")
    public ResponseEntity<?> rename(HttpServletRequest request,@PathVariable("id") Long id,@RequestParam String denomination){
        String token = JwtAuthenticationFilter.getTokenFromRequest(request);
        Object response = menuItemService.rename(token,id, denomination);
        return ResponseEntity.ok(new ApiResponse(response));
    }

    @PostMapping("/{id}/change-category")
    public ResponseEntity<?> changeCategory(HttpServletRequest request,@PathVariable("id") Long id,@RequestParam Long c_id){
        String token = JwtAuthenticationFilter.getTokenFromRequest(request);
        Object response = menuItemService.changeCategory(token,id, c_id);
        return ResponseEntity.ok(new ApiResponse(response));
    }

    @PostMapping("/{id}/change-presentation")
    public ResponseEntity<?> changePresentation(HttpServletRequest request,@PathVariable("id") Long id,@RequestParam Long p_id){
        String token = JwtAuthenticationFilter.getTokenFromRequest(request);
        Object response = menuItemService.changePresentation(token,id, p_id);
        return ResponseEntity.ok(new ApiResponse(response));
    }

    @PostMapping("/{id}/edit-price")
    public ResponseEntity<?> editPrice(HttpServletRequest request,@PathVariable("id") Long id,@RequestParam Double price){
        String token = JwtAuthenticationFilter.getTokenFromRequest(request);
        Object response = menuItemService.editPrice(token,id, price);
        return ResponseEntity.ok(new ApiResponse(response));
    }

    @PostMapping("/{id}/change-dispatch-area")
    public ResponseEntity<?> changeDispatchArea(HttpServletRequest request,@PathVariable("id") Long id,@RequestParam Long d_id){
        String token = JwtAuthenticationFilter.getTokenFromRequest(request);
        Object response = menuItemService.changeDispatchArea(token,id, d_id);
        return ResponseEntity.ok(new ApiResponse(response));
    }
}
