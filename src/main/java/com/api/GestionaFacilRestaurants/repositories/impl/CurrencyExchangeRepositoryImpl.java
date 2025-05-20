package com.api.GestionaFacilRestaurants.repositories.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import com.api.GestionaFacilRestaurants.repositories.CurrencyExchangeRepository;

@Repository
public class CurrencyExchangeRepositoryImpl implements CurrencyExchangeRepository {
    private static final String API_URL = "https://apiperu.dev/api/tipo_de_cambio";
    private static final String TOKEN = "b376edb9a7261cde7d96875931c994a7937ba88537416808741d7bc010d96845";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Double findExchange(String fecha, String moneda) {
        // Configurar encabezados
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + TOKEN);

        // Crear el cuerpo de la solicitud
        Map<String, String> body = new HashMap<>();
        body.put("fecha", fecha);
        body.put("moneda",moneda);

        // Crear la entidad de la solicitud (headers + body)
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);

         // Hacer la solicitud POST y obtener la respuesta como Object
        ResponseEntity<Object> response = restTemplate.exchange(
                API_URL,
                HttpMethod.POST,
                requestEntity,
                Object.class
        );

        @SuppressWarnings("unchecked")
        Map<String, Object> responseMap = (Map<String,Object>) response.getBody();
        @SuppressWarnings("null")
        boolean success = (Boolean) responseMap.get("success");
        
        if(success){
            @SuppressWarnings("unchecked")
            Map<String,Object> data = (Map<String,Object>) responseMap.get("data");
            Double compra = (Double) data.get("compra");
            return compra;
        }
        return null;
    }


}
