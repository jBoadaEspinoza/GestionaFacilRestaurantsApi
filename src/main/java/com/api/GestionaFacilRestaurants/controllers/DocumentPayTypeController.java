package com.api.GestionaFacilRestaurants.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.GestionaFacilRestaurants.responses.ApiResponse;
import com.api.GestionaFacilRestaurants.security.JwtAuthenticationFilter;
import com.api.GestionaFacilRestaurants.services.DocumentPayTypeService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("${api.base.path}/document-pay-types")
public class DocumentPayTypeController {
    @Autowired
    private DocumentPayTypeService documentPayTypeService;

    @GetMapping("{id}/getPersonalDocumentTypes")
    public ResponseEntity<?> getPersonalDocumentTypes(HttpServletRequest request,@PathVariable("id") Long id){
        String token = JwtAuthenticationFilter.getTokenFromRequest(request);
        Object response = documentPayTypeService.getPersonalDocumentTypes(token,id);
        return ResponseEntity.ok(new ApiResponse(response));
    }

}
