package com.api.GestionaFacilRestaurants.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.GestionaFacilRestaurants.responses.ApiResponse;
import com.api.GestionaFacilRestaurants.security.JwtAuthenticationFilter;
import com.api.GestionaFacilRestaurants.services.ComandaService;
import com.api.GestionaFacilRestaurants.utilities.KeyUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("${api.base.path}/comandas")
public class ComandaController {
    @Autowired
    private ComandaService comandaService;

    @GetMapping("/{token}/getPdf/ticket58mm/{ruc}-{numbering}.pdf")
    public void getPDF(HttpServletResponse response,@PathVariable("token") String token,@PathVariable("ruc") String ruc,@PathVariable("numbering") String numbering) {
        comandaService.getTicket58mm(KeyUtil.decrypt(token),ruc,numbering,response);
    }
    @PostMapping("/{id}")
    public ResponseEntity<?> generate(HttpServletRequest request,@PathVariable("id") Long id){
        String token = JwtAuthenticationFilter.getTokenFromRequest(request);
        Object response = comandaService.generate(token,id);
        return ResponseEntity.ok(new ApiResponse(response));
    } 
}
