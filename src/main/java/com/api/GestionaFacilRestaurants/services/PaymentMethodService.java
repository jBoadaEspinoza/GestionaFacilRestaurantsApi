package com.api.GestionaFacilRestaurants.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.api.GestionaFacilRestaurants.models.PaymentMethod;
import com.api.GestionaFacilRestaurants.repositories.PaymentMethodRepository;
import com.api.GestionaFacilRestaurants.responses.SuccessResponse;
import com.api.GestionaFacilRestaurants.utilities.JwtUtil;

@Service
public class PaymentMethodService {
    @Autowired
    private JwtUtil jwt;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    public Object get(String token){
        String tokenUpdated = jwt.extendTokenExpiration(token);
        List<PaymentMethod> data = paymentMethodRepository.findAll();
        return new SuccessResponse(data,tokenUpdated);
    }
    
}
