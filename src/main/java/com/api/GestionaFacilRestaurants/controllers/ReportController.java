package com.api.GestionaFacilRestaurants.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.api.GestionaFacilRestaurants.services.ReportService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("${api.base.path}/reports")
public class ReportController {
    @Autowired
    private ReportService reportService;
    
    @GetMapping("/cash-opening/{id}/getPDF/58mm/{fileName}.pdf")
    public void getReportCashOpening(HttpServletResponse response,@PathVariable("id") String id,@PathVariable("fileName") String fileName){
        
        reportService.getCashOpening(id,fileName,response);
    }
}
