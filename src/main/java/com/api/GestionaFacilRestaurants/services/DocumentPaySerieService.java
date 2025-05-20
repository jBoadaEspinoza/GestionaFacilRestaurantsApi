package com.api.GestionaFacilRestaurants.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.GestionaFacilRestaurants.models.Business;
import com.api.GestionaFacilRestaurants.models.DocumentPaySerie;
import com.api.GestionaFacilRestaurants.repositories.BusinessRepository;
import com.api.GestionaFacilRestaurants.repositories.DocumentPaySerieRepository;
import com.api.GestionaFacilRestaurants.responses.ErrorResponse;
import com.api.GestionaFacilRestaurants.responses.SuccessResponse;
import com.api.GestionaFacilRestaurants.utilities.JwtUtil;

@Service
public class DocumentPaySerieService {
    @Autowired
    private JwtUtil jwt;
    
    @Autowired 
    private DocumentPaySerieRepository documentPaySerieRepository;
    
    @Autowired
    private BusinessRepository businessRepository;

    public Object get(String token){
        Long ruc = jwt.extractBusinessRuc(token);
        String tokenUpdate = jwt.extendTokenExpiration(token);
        Business businessFinded = businessRepository.findByRuc(ruc).orElse(null);
        if(businessFinded==null){
            return new ErrorResponse("business_not_found","Establecimiento no encontrado");
        }
        List<DocumentPaySerie> data = documentPaySerieRepository.findAllByBusinessIdOrderByIdDesc(businessFinded.getId());
        return new SuccessResponse(data,tokenUpdate);
    }

}
