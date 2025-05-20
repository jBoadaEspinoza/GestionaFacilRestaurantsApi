package com.api.GestionaFacilRestaurants.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailRequest {
    private String to;        // destino
    private String subject;   // asunto
    private String body;      // cuerpo (puede ser texto plano o HTML)
    private boolean isHtml;   // indica si el cuerpo es HTML o texto plano
}
