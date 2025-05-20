package com.api.GestionaFacilRestaurants.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.GestionaFacilRestaurants.repositories.EmailRepository;

@Service
public class EmailService {
    @Autowired
    private EmailRepository emailRepository;

    public boolean sendTextEmail(String to, String subject, String textBody) {
        return emailRepository.sendPlainTextEmail(to, subject, textBody);
    }
}
