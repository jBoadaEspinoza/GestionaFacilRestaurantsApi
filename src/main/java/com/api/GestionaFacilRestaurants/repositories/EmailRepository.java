package com.api.GestionaFacilRestaurants.repositories;

import java.io.File;

public interface EmailRepository {
     boolean sendEmail(String to, String subject, String htmlBody);
     boolean sendEmailWithAttachment(String to, String subject, String htmlBody, File file);
     boolean sendPlainTextEmail(String to, String subject, String textBody); // sin formato (texto plano)
}
