package com.api.GestionaFacilRestaurants.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.GestionaFacilRestaurants.services.DocumentService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("${api.base.path}/documents")
public class DocumentController {
    @Autowired
    private DocumentService documentService;
    @GetMapping("/{id}/getPDF/ticket58mm/{fileName}.pdf")
    public void getPDF(HttpServletResponse response,@PathVariable("id") String id,@PathVariable("fileName") String fileName){
        documentService.getPDF(id,fileName,response);
    }
}
