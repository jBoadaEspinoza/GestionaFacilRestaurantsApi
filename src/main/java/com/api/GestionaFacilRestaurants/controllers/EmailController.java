package com.api.GestionaFacilRestaurants.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.GestionaFacilRestaurants.requests.EmailRequest;
import com.api.GestionaFacilRestaurants.responses.ApiResponse;
import com.api.GestionaFacilRestaurants.services.EmailService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("${api.base.path}/email")
public class EmailController {
    @Autowired
    private EmailService emailService;

    // Endpoint to send a plain text email
    @PostMapping("/send")
    public ResponseEntity<?> sendTextEmail(HttpServletRequest request,@RequestBody EmailRequest emailRequest) {
        String to = emailRequest.getTo();
        String subject = emailRequest.getSubject();
        String textBody = emailRequest.getBody();

        boolean success = emailService.sendTextEmail(to, subject, textBody);

        if (success) {
            return ResponseEntity.ok(new ApiResponse("Email sent successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Failed to send email"));
        }
    }
}
